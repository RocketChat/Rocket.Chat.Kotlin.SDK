package chat.rocket.core.internal.rest

import chat.rocket.common.*
import chat.rocket.common.internal.AuthenticationErrorMessage
import chat.rocket.common.internal.ErrorMessage
import chat.rocket.common.model.BaseRoom
import chat.rocket.common.model.ServerInfo
import chat.rocket.common.model.Token
import chat.rocket.common.util.Logger
import chat.rocket.core.RocketChatClient
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.*
import java.io.IOException
import java.lang.reflect.Type

fun RocketChatClient.serverInfo(success: (ServerInfo) -> Unit, error: (RocketChatException) -> Unit) {
    val url = restUrl.newBuilder()
                        .addPathSegment("api")
                        .addPathSegment("info")
                        .build()

    val request = Request.Builder().url(url).get().build()

    handleRestCall(this, request, ServerInfo::class.java, success, error)
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

internal fun <T> handleRestCall(client: RocketChatClient, request: Request,
                                type: Type, valueCallback: (T) -> Unit,
                                errorCallback: (RocketChatException) -> Unit) {
    client.httpClient.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: Call, e: IOException) {
            errorCallback.invoke(RocketChatNetworkErrorException("network error", e))
        }

        @Throws(IOException::class) override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful) {
                errorCallback.invoke(processCallbackError(client.moshi, response, client.logger))
                return
            }

            try {
                val adapter: JsonAdapter<T>? = client.moshi.adapter(type)
                val data : T = adapter!!.fromJson(response.body()!!.source())!!
                valueCallback.invoke(data)
            } catch (e: IOException) {
                errorCallback.invoke(RocketChatInvalidResponseException(e.message!!, e))
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
            exception = RocketChatApiException(response.code(), message?.error ?: "error",
                    message?.errorType ?: "errorType")
        }
    } catch (e: IOException) {
        exception = RocketChatException(e.message!!, e)
    } catch (e: NullPointerException) {
        exception = RocketChatException(e.message!!, e)
    }

    return exception
}