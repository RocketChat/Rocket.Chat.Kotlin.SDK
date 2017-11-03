package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.common.internal.RestResult
import chat.rocket.core.RocketChatClient
import chat.rocket.core.model.Message
import com.squareup.moshi.Types
import okhttp3.FormBody

fun RocketChatClient.pinMessage(messageId: String, success: (Message) -> Unit,
                                error: (RocketChatException) -> Unit) {
    val body = FormBody.Builder().add("messageId", messageId).build()

    val httpUrl = requestUrl(restUrl, "chat.pinMessage").build()

    val request = requestBuilder(httpUrl).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Message::class.java)

    handleSimpleRestCall<RestResult<Message>>(this, request, type, {
        success.invoke(it.result())
    }, error)
}