package chat.rocket.core.internal.rest

import chat.rocket.common.model.BaseResult
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.User
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.ChatRoomJoinPayload
import chat.rocket.core.internal.model.ChatRoomPayload
import chat.rocket.core.model.ChatRoomRole
import chat.rocket.core.model.Message
import chat.rocket.core.model.PagedResult
import com.squareup.moshi.Types
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import okhttp3.RequestBody

/**
 * Returns the list of members of a chat room.
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param offset The offset to paging which specifies the first entry to return from a collection.
 * @param count The amount of item to return from a collection.
 * @return The list of members of a chat room.
 */
suspend fun RocketChatClient.getMembers(
    roomId: String,
    roomType: RoomType,
    offset: Long,
    count: Long
): PagedResult<List<User>> = withContext(CommonPool) {
    val httpUrl = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "members"))
        .addQueryParameter("roomId", roomId)
        .addQueryParameter("offset", offset.toString())
        .addQueryParameter("count", count.toString())
        .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, User::class.java)
    )
    val result = handleRestCall<RestResult<List<User>>>(request, type)

    return@withContext PagedResult<List<User>>(
        result.result(),
        result.total() ?: 0,
        result.offset() ?: 0
    )
}

/**
 * Returns the list of favorites messages of a chat room.
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param offset The offset to paging which specifies the first entry to return from a collection.
 * @return The list of favorites messages of a chat room.
 */
suspend fun RocketChatClient.getFavoriteMessages(
    roomId: String,
    roomType: RoomType,
    offset: Int
): PagedResult<List<Message>> = withContext(CommonPool) {
    val userId = tokenRepository.get(this.url)?.userId

    val httpUrl = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "messages"))
        .addQueryParameter("roomId", roomId)
        .addQueryParameter("offset", offset.toString())
        .addQueryParameter("query", "{\"starred._id\":{\"\$in\":[\"$userId\"]}}")
        .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, Message::class.java)
    )
    val result = handleRestCall<RestResult<List<Message>>>(request, type)

    return@withContext PagedResult<List<Message>>(
        result.result(),
        result.total() ?: 0,
        result.offset() ?: 0
    )
}

/**
 * Returns the list of pinned messages of a chat room.
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param offset The offset to paging which specifies the first entry to return from a collection.
 * @return The list of pinned messages of a chat room.
 */
suspend fun RocketChatClient.getPinnedMessages(
    roomId: String,
    roomType: RoomType,
    offset: Int? = 0
): PagedResult<List<Message>> = withContext(CommonPool) {
    val httpUrl = requestUrl(
        restUrl,
        getRestApiMethodNameByRoomType(roomType, "messages")
    )
        .addQueryParameter("roomId", roomId)
        .addQueryParameter("offset", offset.toString())
        .addQueryParameter("query", "{\"pinned\":true}")
        .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, Message::class.java)
    )
    val result = handleRestCall<RestResult<List<Message>>>(request, type)

    return@withContext PagedResult<List<Message>>(
        result.result(),
        result.total() ?: 0,
        result.offset() ?: 0
    )
}

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

// TODO: Add doc.
suspend fun RocketChatClient.joinChat(roomId: String): Boolean = withContext(CommonPool) {
    val payload = ChatRoomJoinPayload(roomId)
    val adapter = moshi.adapter(ChatRoomJoinPayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "channels.join").build()
    val request = requestBuilder(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

/**
 * Returns the list of user of a chat room that satisfies a query.
 *
 * @param queryParam Parameter which is used to query users on the basis of regex.
 * @param count The number of users to be returned in the result
 * @param offset The number of users to skip from the beginning
 * @return The list of user of a chat room that satisfies a query.
 */
suspend fun RocketChatClient.queryUsers(
    queryParam: String,
    count: Long = 30,
    offset: Long = 0
): PagedResult<List<User>> = withContext(CommonPool) {
    val httpUrl = requestUrl(restUrl, "users.list")
        .addQueryParameter("query", "{ \"name\": { \"\\u0024regex\": \"$queryParam\" } }")
        .addQueryParameter("offset", offset.toString())
        .addQueryParameter("count", count.toString())
        .build()
    val request = requestBuilder(httpUrl).get().build()
    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, User::class.java)
    )

    val result = handleRestCall<RestResult<List<User>>>(request, type)
    return@withContext PagedResult<List<User>>(
        result.result(),
        result.total() ?: 0,
        result.offset() ?: 0
    )
}

/**
 * Return a list of users in a channel [roomName] that have roles other than 'user' on it.
 *
 * @param roomType Type of the room (DIRECT, GROUP, CHANNEL, etc)
 * @param roomName Name of the room to query user's roles.
 *
 * @return List of [ChatRoomRole] objects.
 */
suspend fun RocketChatClient.chatRoomRoles(
    roomType: RoomType,
    roomName: String
): List<ChatRoomRole> = withContext(CommonPool) {

    val httpUrl = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "roles"))
        .addQueryParameter("roomName", roomName)
        .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, ChatRoomRole::class.java)
    )
    return@withContext handleRestCall<RestResult<List<ChatRoomRole>>>(request, type).result()
}