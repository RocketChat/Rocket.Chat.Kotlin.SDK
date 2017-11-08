package chat.rocket.core.internal.rest

import chat.rocket.common.*
import chat.rocket.common.internal.AuthenticationErrorMessage
import chat.rocket.common.internal.ErrorMessage
import chat.rocket.common.model.BaseRoom
import chat.rocket.common.model.ServerInfo
import chat.rocket.common.model.Token
import chat.rocket.common.util.Logger
import chat.rocket.common.util.ifNull
import chat.rocket.core.RocketChatClient
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type

fun RocketChatClient.serverInfo(success: (ServerInfo) -> Unit, error: (RocketChatException) -> Unit) {
    val url = restUrl.newBuilder()
                        .addPathSegment("api")
                        .addPathSegment("info")
                        .build()

    val request = Request.Builder().url(url).get().build()

    handleRestCall(request, ServerInfo::class.java, success, error)
}

internal fun getRestApiMethodNameByRoomType(roomType: BaseRoom.RoomType, method: String): String {
    when (roomType) {
        BaseRoom.RoomType.PUBLIC -> return "channels." + method
        BaseRoom.RoomType.PRIVATE -> return "groups." + method
        else -> return "dm." + method
    }
}

internal fun requestUrl(baseUrl: HttpUrl, method: String): HttpUrl.Builder {
    return baseUrl.newBuilder()
            .addPathSegment("api")
            .addPathSegment("v1")
            .addPathSegment(method)
}

fun RocketChatClient.requestBuilder(httpUrl: HttpUrl): Request.Builder {
    val builder = Request.Builder().url(httpUrl)

    val token: Token? = tokenProvider.get()
    token?.let {
        builder.addHeader("X-Auth-Token", token.authToken)
                .addHeader("X-User-Id", token.userId)
    }

    return builder
}

internal fun <T> RocketChatClient.handleRestCall(request: Request,
                                type: Type, valueCallback: (T) -> Unit,
                                errorCallback: (RocketChatException) -> Unit) {
    httpClient.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: Call, e: IOException) {
            errorCallback.invoke(RocketChatNetworkErrorException("network error", e))
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful) {
                errorCallback.invoke(processCallbackError(moshi, response, logger))
                return
            }

            try {
                // Override nullability, if there is no adapter, moshi will throw...
                val adapter: JsonAdapter<T> = moshi.adapter(type)!!

                response.body()?.let {
                    it.source()
                }?.let {
                    adapter.fromJson(it)
                }?.let(valueCallback).ifNull {
                    errorCallback.invoke(RocketChatInvalidResponseException("Error parsing JSON message"))
                }
            } catch (ex: Exception) {
                // kinda of multicatch exception...
                when(ex) {
                    is JsonDataException,
                    is IllegalArgumentException,
                    is IOException -> {
                        errorCallback.invoke(RocketChatInvalidResponseException(ex.message!!, ex))
                    }
                    else -> throw ex
                }
            }
        }
    })
}

private fun processCallbackError(moshi: Moshi, response: Response, logger: Logger) : RocketChatException {
    var exception: RocketChatException
    try {
        val body = response.body()?.string() ?: "missing body"
        logger.debug {"Error body: ${body}"}
        if (response.code() == 401) {
            val adapter: JsonAdapter<AuthenticationErrorMessage>? = moshi.adapter(AuthenticationErrorMessage::class.java)
            val message: AuthenticationErrorMessage? = adapter?.fromJson(body)
            exception = RocketChatAuthException(message?.message ?: "Authentication problem")
        } else {
            val adapter: JsonAdapter<ErrorMessage>? = moshi.adapter(ErrorMessage::class.java)
            val message = adapter?.fromJson(body)
            exception = RocketChatApiException(message?.errorType ?: response.code().toString(),
                    message?.error ?: "unknown error")
        }
    } catch (e: IOException) {
        exception = RocketChatApiException(response.code().toString(), e.message!!, e)
    } catch (e: NullPointerException) {
        exception = RocketChatApiException(response.code().toString(), e.message!!, e)
    }

    return exception
}