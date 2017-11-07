package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.common.model.BaseRoom
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
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

    handleRestCall<RestResult<Message>>(this, request, type, {
        success.invoke(it.result())
    }, error)
}

fun RocketChatClient.getRoomFavoriteMessages(roomId: String,
                                             roomType: BaseRoom.RoomType,
                                             offset: Int,
                                             success: (List<Message>, Long) -> Unit,
                                             error: (RocketChatException) -> Unit) {
    val userId = tokenProvider.get()?.userId

    val httpUrl = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "messages"))
            .addQueryParameter("roomId", roomId)
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("query", "{\"starred._id\":{\"\$in\":[\"$userId\"]}}")
            .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Types.newParameterizedType(List::class.java, Message::class.java))
    handleRestCall<RestResult<List<Message>>>(this, request, type, {
        success.invoke(it.result(), it.total() ?: 0)
    }, error)
}

fun RocketChatClient.getRoomPinnedMessages(roomId: String,
                                           roomType: BaseRoom.RoomType,
                                           offset: Int,
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
    handleRestCall<RestResult<List<Message>>>(this, request, type, {
        success.invoke(it.result(), it.total() ?: 0)
    }, error)
}