package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.common.model.BaseRoom
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.MessagePayload
import chat.rocket.core.model.Attachment
import chat.rocket.core.model.Message
import com.squareup.moshi.Types
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.RequestBody

fun RocketChatClient.pinMessage(messageId: String, success: (Message) -> Unit,
                                error: (RocketChatException) -> Unit) {
    val body = FormBody.Builder().add("messageId", messageId).build()

    val httpUrl = requestUrl(restUrl, "chat.pinMessage").build()

    val request = requestBuilder(httpUrl).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Message::class.java)

    handleRestCall<RestResult<Message>>(request, type, {
        success(it.result())
    }, error)
}

fun RocketChatClient.getRoomFavoriteMessages(roomId: String,
                                             roomType: BaseRoom.RoomType,
                                             offset: Int,
                                             success: (List<Message>, Long) -> Unit,
                                             error: (RocketChatException) -> Unit) {
    val userId = tokenRepository.get()?.userId

    val httpUrl = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "messages"))
            .addQueryParameter("roomId", roomId)
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("query", "{\"starred._id\":{\"\$in\":[\"$userId\"]}}")
            .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Types.newParameterizedType(List::class.java, Message::class.java))
    handleRestCall<RestResult<List<Message>>>(request, type, {
        success(it.result(), it.total() ?: 0)
    }, error)
}

fun RocketChatClient.getRoomPinnedMessages(roomId: String,
                                           roomType: BaseRoom.RoomType,
                                           offset: Int? = 0,
                                           success: (List<Message>, Long) -> Unit,
                                           error: (RocketChatException) -> Unit) {
    val httpUrl = requestUrl(restUrl,
            getRestApiMethodNameByRoomType(roomType, "messages"))
            .addQueryParameter("roomId", roomId)
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("query", "{\"pinned\":true}")
            .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Types.newParameterizedType(List::class.java, Message::class.java))
    handleRestCall<RestResult<List<Message>>>(request, type, {
        success(it.result(), it.total() ?: 0)
    }, error)
}

/**
 * Sends a new message
 *
 * @param roomId the room where to send the message (works with all types)
 * @param text Optional text message to send
 * @param alias Optianal alias to be used as the sender of the message
 * @param emoji Optional emoji to be used as the sender's avatar
 * @param avatar Optional avatar url to be used as the sender's avatar
 * @param attachments Optional List of [Attachment]
 * @param success Message sent success callback
 * @param error error callback
 */
fun RocketChatClient.sendMessage(roomId: String,
                                 text: String? = null,
                                 alias: String? = null,
                                 emoji: String? = null,
                                 avatar: String? = null,
                                 attachments: List<Attachment>? = null,
                                 success: (Message) -> Unit,
                                 error: (RocketChatException) -> Unit) {
    val payload = MessagePayload(roomId, text, alias, emoji, avatar, attachments)
    val adapter = moshi.adapter(MessagePayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val contentType = MediaType.parse("application/json; charset=utf-8")
    val body = RequestBody.create(contentType, payloadBody)

    val url = requestUrl(restUrl, "chat.postMessage").build()
    val request = requestBuilder(url).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, Message::class.java)
    handleRestCall<RestResult<Message>>(request, type, {
        success(it.result())
    }, error)
}