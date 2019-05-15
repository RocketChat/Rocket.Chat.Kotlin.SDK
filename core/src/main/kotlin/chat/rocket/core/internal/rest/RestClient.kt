package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatApiException
import chat.rocket.common.RocketChatAuthException
import chat.rocket.common.RocketChatException
import chat.rocket.common.RocketChatInvalidProtocolException
import chat.rocket.common.RocketChatInvalidResponseException
import chat.rocket.common.RocketChatNetworkErrorException
import chat.rocket.common.RocketChatTwoFactorException
import chat.rocket.common.internal.AuthenticationErrorMessage
import chat.rocket.common.internal.ErrorMessage
import chat.rocket.common.model.RoomType
import chat.rocket.common.util.Logger
import chat.rocket.core.RocketChatClient
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
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
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal fun getRestApiMethodNameByRoomType(roomType: RoomType, method: String): String {
    return when (roomType) {
        is RoomType.Channel -> "channels.$method"
        is RoomType.PrivateGroup -> "groups.$method"
        is RoomType.DirectMessage -> "im.$method"
    // TODO - handle custom rooms
        else -> "channels.$method"
    }
}

internal fun requestUrl(baseUrl: HttpUrl, method: String): HttpUrl.Builder {
    return baseUrl.newBuilder()
        .addPathSegment("api")
        .addPathSegment("v1")
        .addPathSegment(method)
}

internal fun requestUrlForVideoConference(baseUrl: HttpUrl, method: String): HttpUrl.Builder {
    return baseUrl.newBuilder()
        .addPathSegment("api")
        .addPathSegment("v1")
        .addPathSegment("video-conference")
        .addPathSegment(method)
}

internal fun RocketChatClient.requestBuilder(httpUrl: HttpUrl): Request.Builder =
    Request.Builder()
        .url(httpUrl)
        .header("User-Agent", agent)
        .tag(Any())

internal fun RocketChatClient.requestBuilderForAuthenticatedMethods(httpUrl: HttpUrl): Request.Builder {
    val builder = requestBuilder(httpUrl)

    tokenRepository.get(this.url)?.let {
        builder.addHeader("X-Auth-Token", it.authToken).addHeader("X-User-Id", it.userId)
    }

    return builder
}

internal fun <T> RocketChatClient.handleResponse(response: Response, type: Type): T {
    val url = response.priorResponse()?.request()?.url() ?: response.request().url()
    try {
        // Override nullability, if there is no adapter, moshi will throw...
        val adapter: JsonAdapter<T> = moshi.adapter(type)!!

        val source = response.body()?.source()
        checkNotNull(source) { "Missing body" }

        return adapter.fromJson(source) ?: throw RocketChatInvalidResponseException("Error parsing JSON message", url = url.toString())
    } catch (ex: Exception) {
        when (ex) {
            is RocketChatException -> throw ex // already a RocketChatException, just rethrow it.
            else -> throw RocketChatInvalidResponseException(ex.message!!, ex, url.toString())
        }
    } finally {
        response.body()?.close()
    }
}

internal suspend fun RocketChatClient.handleRequest(
    request: Request,
    largeFile: Boolean = false,
    allowRedirects: Boolean = true
): Response = suspendCancellableCoroutine { continuation ->
    val callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            logger.debug {
                "Failed request: ${request.method()} - ${request.url()} - ${e.message}"
            }
            continuation.tryResumeWithException {
                RocketChatNetworkErrorException("Network Error: ${e.message}", e, request.url().toString())
            }
        }

        override fun onResponse(call: Call, response: Response) {
            logger.debug {
                "Successful HTTP request: ${request.method()} - ${request.url()}: ${response.code()} ${response.message()}"
            }
            if (!response.isSuccessful) {
                continuation.tryResumeWithException {
                    processCallbackError(moshi, request, response, logger, allowRedirects)
                }
            } else {
                continuation.tryToResume { response }
            }
        }
    }

    logger.debug {
        "Enqueueing: ${request.method()} - ${request.url()}"
    }

    val client = ensureClient(largeFile, allowRedirects)
    client.newCall(request).enqueue(callback)

    continuation.invokeOnCancellation {
        client.cancel(request.tag())
    }
}

internal suspend fun <T> RocketChatClient.handleRestCall(
    request: Request,
    type: Type,
    largeFile: Boolean = false,
    allowRedirects: Boolean = true
): T {
    val response = handleRequest(request, largeFile, allowRedirects)
    return handleResponse(response, type)
}

internal fun RocketChatClient.ensureClient(largeFile: Boolean, allowRedirects: Boolean): OkHttpClient {
    return if (largeFile || !allowRedirects) {
        httpClient.newBuilder().apply {
            if (largeFile) {
                writeTimeout(90, TimeUnit.SECONDS)
                readTimeout(90, TimeUnit.SECONDS)
            }
            followRedirects(allowRedirects)
        }.build()
    } else {
        httpClient
    }
}

internal fun processCallbackError(
    moshi: Moshi,
    request: Request,
    response: Response,
    logger: Logger,
    allowRedirects: Boolean = true
): RocketChatException {
    var exception: RocketChatException
    try {
        if (response.isRedirect && !allowRedirects) {
            exception = RocketChatInvalidProtocolException("Invalid Protocol", url = request.url().toString())
        } else {
            val body = response.body()?.string() ?: "missing body"
            logger.debug { "Error body: $body" }
            exception = if (response.code() == 401) {
                val adapter: JsonAdapter<AuthenticationErrorMessage>? = moshi.adapter(AuthenticationErrorMessage::class.java)
                val message: AuthenticationErrorMessage? = adapter?.fromJson(body)
                if (message?.error?.contentEquals("totp-required") == true)
                    RocketChatTwoFactorException(message.message, request.url().toString())
                else
                    RocketChatAuthException(message?.message ?: "Authentication problem", request.url().toString())
            } else {
                val adapter: JsonAdapter<ErrorMessage>? = moshi.adapter(ErrorMessage::class.java)
                val message = adapter?.fromJson(body)
                RocketChatApiException(message?.errorType ?: response.code().toString(), message?.error
                    ?: "unknown error",
                    url = request.url().toString())
            }
        }
    } catch (e: Exception) {
        exception = RocketChatApiException(response.code().toString(), e.message!!, e, request.url().toString())
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

private fun OkHttpClient.cancel(tag: Any?) {
    tag?.let {
        dispatcher().queuedCalls().filter { tag == it.request().tag() }.forEach { it.cancel() }
        dispatcher().runningCalls().filter { tag == it.request().tag() }.forEach { it.cancel() }
    }
}

internal val MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8")
