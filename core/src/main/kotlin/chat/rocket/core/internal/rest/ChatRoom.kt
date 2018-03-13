package chat.rocket.core.internal.rest

import chat.rocket.common.model.BaseResult
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.User
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.ChatRoomJoinPayload
import chat.rocket.core.internal.model.ChatRoomPayload
import chat.rocket.core.model.PagedResult
import com.squareup.moshi.Types
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import okhttp3.RequestBody

/**
 * Marks a room as read.
 *
 * @param roomId The ID of the room.
 */
suspend fun RocketChatClient.markAsRead(roomId: String) {
    withContext(CommonPool) {
        val payload = ChatRoomPayload(roomId)
        val adapter = moshi.adapter(ChatRoomPayload::class.java)
        val payloadBody = adapter.toJson(payload)

        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, "subscriptions.read").build()
        val request = requestBuilder(url).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}

/**
 * Returns the list of members of a ChatRoom.
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param offset The offset to paging which specifies the first entry to return from a collection.
 * @param count The amount of item to return from a collection.
 */
suspend fun RocketChatClient.getMembers(roomId: String, roomType: RoomType, offset: Long, count: Long): PagedResult<List<User>> = withContext(CommonPool) {
    val httpUrl = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "members"))
            .addQueryParameter("roomId", roomId)
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("count", count.toString())
            .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Types.newParameterizedType(List::class.java, User::class.java))
    val result = handleRestCall<RestResult<List<User>>>(request, type)

    return@withContext PagedResult<List<User>>(result.result(), result.total() ?: 0, result.offset() ?: 0)
}

suspend fun RocketChatClient.joinChat(roomId: String): Boolean = withContext(CommonPool) {
    val payload = ChatRoomJoinPayload(roomId)
    val adapter = moshi.adapter(ChatRoomJoinPayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "channels.join").build()
    val request = requestBuilder(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}