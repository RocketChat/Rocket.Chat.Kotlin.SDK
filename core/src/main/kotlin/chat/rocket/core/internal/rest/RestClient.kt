package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatApiException
import chat.rocket.common.RocketChatAuthException
import chat.rocket.common.RocketChatException
import chat.rocket.common.RocketChatInvalidResponseException
import chat.rocket.common.RocketChatNetworkErrorException
import chat.rocket.common.RocketChatTwoFactorException
import chat.rocket.common.internal.AuthenticationErrorMessage
import chat.rocket.common.internal.ErrorMessage
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.Token
import chat.rocket.common.util.Logger
import chat.rocket.common.util.ifNull
import chat.rocket.core.RocketChatClient
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import kotlinx.coroutines.experimental.CancellableContinuation
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

internal fun getRestApiMethodNameByRoomType(roomType: RoomType, method: String): String {
    return when (roomType) {
        is RoomType.Channel -> "channels.$method"
        is RoomType.PrivateGroup -> "groups.$method"
        is RoomType.DirectMessage -> "dm.$method"
        // TODO - handle custom rooms
        else -> "channels." + method
    }
}

internal fun requestUrl(baseUrl: HttpUrl, method: String): HttpUrl.Builder {
    return baseUrl.newBuilder()
            .addPathSegment("api")
            .addPathSegment("v1")
            .addPathSegment(method)
}

internal fun RocketChatClient.requestBuilder(httpUrl: HttpUrl): Request.Builder {
    val builder = Request.Builder().url(httpUrl)

    val token: Token? = tokenRepository.get()
    token?.let {
        builder.addHeader("X-Auth-Token", token.authToken)
                .addHeader("X-User-Id", token.userId)
    }

    return builder
}

internal suspend fun <T> RocketChatClient.handleRestCall(request: Request, type: Type, largeFile: Boolean = false): T =
        suspendCancellableCoroutine { continuation ->

            val callback = object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    logger.debug {
                        "Failed request: $request"
                    }
                    continuation.tryResumeWithException {
                        RocketChatNetworkErrorException("Network Error: ${e.message}", e)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        continuation.tryResumeWithException {
                            processCallbackError(moshi, response, logger)
                        }
                        return
                    }

                    try {
                        // Override nullability, if there is no adapter, moshi will throw...
                        val adapter: JsonAdapter<T> = moshi.adapter(type)!!

                        response.body()?.source()?.let { source ->
                            adapter.fromJson(source)?.let {
                                value -> continuation.tryToResume { value }
                            }.ifNull {
                                continuation.tryResumeWithException {
                                    RocketChatInvalidResponseException("Error parsing JSON message")
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        // kinda of multi catch exception...
                        when (ex) {
                            is JsonDataException,
                            is IllegalArgumentException,
                            is IOException -> {
                                continuation.tryResumeWithException {
                                    RocketChatInvalidResponseException(ex.message!!, ex)
                                }
                            }
                            else -> continuation.resumeWithException(ex)
                        }
                    } finally {
                        response.body()?.close()
                    }
                }
            }

            if (largeFile) {
                httpClient.newBuilder()
                    .writeTimeout(90, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .build().newCall(request).enqueue(callback)
            } else {
                httpClient.newCall(request).enqueue(callback)
            }

            continuation.invokeOnCompletion {
                if (continuation.isCancelled) httpClient.cancel(request.tag())
            }
        }

internal fun processCallbackError(moshi: Moshi, response: Response, logger: Logger): RocketChatException {
    var exception: RocketChatException
    try {
        val body = response.body()?.string() ?: "missing body"
        logger.debug { "Error body: $body" }
        exception = if (response.code() == 401) {
            val adapter: JsonAdapter<AuthenticationErrorMessage>? = moshi.adapter(AuthenticationErrorMessage::class.java)
            val message: AuthenticationErrorMessage? = adapter?.fromJson(body)
            if (message?.error?.contentEquals("totp-required") == true)
                RocketChatTwoFactorException(message.message)
            else
                RocketChatAuthException(message?.message ?: "Authentication problem")
        } else {
            val adapter: JsonAdapter<ErrorMessage>? = moshi.adapter(ErrorMessage::class.java)
            val message = adapter?.fromJson(body)
            RocketChatApiException(message?.errorType ?: response.code().toString(), message?.error ?: "unknown error")
        }
    } catch (e: IOException) {
        exception = RocketChatApiException(response.code().toString(), e.message!!, e)
    } catch (e: NullPointerException) {
        exception = RocketChatApiException(response.code().toString(), e.message!!, e)
    } finally {
        response.body()?.close()
    }

    return exception
}

private inline fun <T> CancellableContinuation<T>.tryToResume(getter: () -> T) {
    isActive || return
    try {
        resume(getter())
    } catch (exception: Throwable) {
        resumeWithException(exception)
    }
}

private inline fun <T> CancellableContinuation<T>.tryResumeWithException(getter: () -> Exception) {
    isActive || return
    resumeWithException(getter())
}

private fun OkHttpClient.cancel(tag: Any) {
    dispatcher().queuedCalls().filter { tag == it.request().tag() }.forEach { it.cancel() }
    dispatcher().runningCalls().filter { tag == it.request().tag() }.forEach { it.cancel() }
}

internal val MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8")