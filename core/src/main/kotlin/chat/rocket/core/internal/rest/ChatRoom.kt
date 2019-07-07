package chat.rocket.core.internal.rest

import chat.rocket.common.model.BaseResult
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.User
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.model.ChatRoomAnnouncementPayload
import chat.rocket.core.internal.model.ChatRoomDescriptionPayload
import chat.rocket.core.internal.model.ChatRoomInvitePayload
import chat.rocket.core.internal.model.ChatRoomJoinCodePayload
import chat.rocket.core.internal.model.ChatRoomKickPayload
import chat.rocket.core.internal.model.ChatRoomNamePayload
import chat.rocket.core.internal.model.ChatRoomPayload
import chat.rocket.core.internal.model.ChatRoomReadOnlyPayload
import chat.rocket.core.internal.model.ChatRoomUnreadPayload
import chat.rocket.core.internal.model.ChatRoomTopicPayload
import chat.rocket.core.internal.model.ChatRoomTypePayload
import chat.rocket.core.internal.model.ChatRoomFavoritePayload
import chat.rocket.core.internal.model.RoomIdPayload
import chat.rocket.core.internal.RestResult
import chat.rocket.core.model.ChatRoomRole
import chat.rocket.core.model.Message
import chat.rocket.core.model.Room
import chat.rocket.core.model.PagedResult
import chat.rocket.core.model.attachment.GenericAttachment
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
): PagedResult<List<User>> = withContext(Dispatchers.IO) {
    val httpUrl = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "members"))
        .addQueryParameter("roomId", roomId)
        .addQueryParameter("offset", offset.toString())
        .addQueryParameter("count", count.toString())
        .build()

    val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()

    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, User::class.java)
    )

    val result = handleRestCall<RestResult<List<User>>>(request, type)
    return@withContext PagedResult<List<User>>(result.result(), result.total() ?: 0, result.offset() ?: 0)
}

/**
 * Returns the list of mentions of a chat room.
 *
 * @param roomId The ID of the room.
 * @param offset The offset to paging which specifies the first entry to return from a collection.
 * @param count The amount of item to return from a collection.
 * @return The list of mentions from the authenticated user of a chat room.
 */
suspend fun RocketChatClient.getMentions(
    roomId: String,
    offset: Long,
    count: Long
): PagedResult<List<Message>> = withContext(Dispatchers.IO) {
    val httpUrl = requestUrl(restUrl, "channels.getAllUserMentionsByChannel")
        .addQueryParameter("roomId", roomId)
        .addQueryParameter("offset", offset.toString())
        .addQueryParameter("count", count.toString())
        .addQueryParameter("sort", "{\"ts\":-1}")
        .build()

    val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()

    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, Message::class.java)
    )

    val result = handleRestCall<RestResult<List<Message>>>(request, type)
    return@withContext PagedResult<List<Message>>(result.result(), result.total() ?: 0, result.offset() ?: 0)
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
): PagedResult<List<Message>> = withContext(Dispatchers.IO) {
    val userId = tokenRepository.get(url)?.userId

    val httpUrl = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "messages"))
        .addQueryParameter("roomId", roomId)
        .addQueryParameter("offset", offset.toString())
        .addQueryParameter("query", "{\"starred._id\":{\"\$in\":[\"$userId\"]}}")
        .build()

    val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()

    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, Message::class.java)
    )

    val result = handleRestCall<RestResult<List<Message>>>(request, type)
    return@withContext PagedResult<List<Message>>(result.result(), result.total() ?: 0, result.offset() ?: 0)
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
): PagedResult<List<Message>> = withContext(Dispatchers.IO) {
    val httpUrl = requestUrl(
        restUrl,
        getRestApiMethodNameByRoomType(roomType, "messages")
    )
        .addQueryParameter("roomId", roomId)
        .addQueryParameter("offset", offset.toString())
        .addQueryParameter("query", "{\"pinned\":true}")
        .build()

    val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()

    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, Message::class.java)
    )

    val result = handleRestCall<RestResult<List<Message>>>(request, type)
    return@withContext PagedResult<List<Message>>(result.result(), result.total() ?: 0, result.offset() ?: 0)
}

/**
 * Returns the list of files of a chat room.
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param offset The offset to paging which specifies the first entry to return from a collection.
 *
 * @return The list of files of a chat room.
 */
suspend fun RocketChatClient.getFiles(
    roomId: String,
    roomType: RoomType,
    offset: Int? = 0
): PagedResult<List<GenericAttachment>> = withContext(Dispatchers.IO) {
    val httpUrl = requestUrl(
        restUrl,
        getRestApiMethodNameByRoomType(roomType, "files")
    )
        .addQueryParameter("roomId", roomId)
        .addQueryParameter("offset", offset.toString())
        .addQueryParameter("sort", "{\"uploadedAt\":-1}")
        .build()

    val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()

    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, GenericAttachment::class.java)
    )

    val result = handleRestCall<RestResult<List<GenericAttachment>>>(request, type)
    return@withContext PagedResult<List<GenericAttachment>>(result.result(), result.total() ?: 0, result.offset() ?: 0)
}

/**
 * Returns the information of a chat room
 *
 * @param roomId The ID of the room.
 * @param roomName The name of the room.
 * @param roomType The type of the room.
 *
 * @return A [Room] object
 */
suspend fun RocketChatClient.getInfo(
    roomId: String,
    roomName: String?,
    roomType: RoomType
): Room = withContext(Dispatchers.IO) {
    val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "info"))
            .addQueryParameter("roomId", roomId)
            .addQueryParameter("roomName", roomName)
            .build()

    val request = requestBuilderForAuthenticatedMethods(url).get().build()

    val type = Types.newParameterizedType(RestResult::class.java, Room::class.java)
    return@withContext handleRestCall<RestResult<Room>>(request, type).result()
}

/**
 * Marks a room as read.
 *
 * @param roomId The ID of the room.
 */
suspend fun RocketChatClient.markAsRead(roomId: String) {
    withContext(Dispatchers.IO) {
        val payload = ChatRoomPayload(roomId)
        val adapter = moshi.adapter(ChatRoomPayload::class.java)
        val payloadBody = adapter.toJson(payload)

        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, "subscriptions.read").build()
        val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}

/**
 * Marks a room as unread.
 *
 * @param roomId The ID of the room.
 */
suspend fun RocketChatClient.markAsUnread(roomId: String) {
    withContext(Dispatchers.IO) {
        val payload = ChatRoomUnreadPayload(roomId)
        val adapter = moshi.adapter(ChatRoomUnreadPayload::class.java)
        val payloadBody = adapter.toJson(payload)

        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, "subscriptions.unread").build()
        val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}

/**
 * Adds a user to the chat room
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param userId userID of the user
 *
 * @return Whether the task was successful or not.
 */
suspend fun RocketChatClient.invite(
    roomId: String,
    roomType: RoomType,
    userId: String
): Boolean = withContext(Dispatchers.IO) {
    val payload = ChatRoomInvitePayload(roomId, userId)
    val adapter = moshi.adapter(ChatRoomInvitePayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "invite")).build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

// TODO: Add doc.
suspend fun RocketChatClient.joinChat(roomId: String): Boolean = withContext(Dispatchers.IO) {
    val payload = RoomIdPayload(roomId)
    val adapter = moshi.adapter(RoomIdPayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "channels.join").build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}


/**
 * Removes a user from the chat room
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param userId userID of the user
 *
 * @return Whether the task was successful or not.
 */
suspend fun RocketChatClient.kick(
    roomId: String,
    roomType: RoomType,
    userId: String
): Boolean = withContext(Dispatchers.IO) {
    val payload = ChatRoomKickPayload(roomId, userId)
    val adapter = moshi.adapter(ChatRoomKickPayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "kick")).build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

/**
 * Leaves a chat room
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 *
 * @return Whether the task was successful or not.
 */
suspend fun RocketChatClient.leaveChat(
    roomId: String,
    roomType: RoomType
): Boolean = withContext(Dispatchers.IO) {
    val payload = RoomIdPayload(roomId)
    val adapter = moshi.adapter(RoomIdPayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "leave")).build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

/**
 * Renames a chat room
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param newName The new name of the room.
 *
 * @return Whether the task was successful or not.
 */
suspend fun RocketChatClient.rename(
    roomId: String,
    roomType: RoomType,
    newName: String
): Boolean = withContext(Dispatchers.IO) {
    val payload = ChatRoomNamePayload(roomId, newName)
    val adapter = moshi.adapter(ChatRoomNamePayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "rename")).build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

/**
 * Sets the chat room to collaborative or read-only
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param readOnly The read-only status of the room.
 *
 * @return Whether the task was successful or not.
 */
suspend fun RocketChatClient.setReadOnly(
    roomId: String,
    roomType: RoomType,
    readOnly: Boolean
) = withContext(Dispatchers.IO) {
    val payload = ChatRoomReadOnlyPayload(roomId, readOnly)
    val adapter = moshi.adapter(ChatRoomReadOnlyPayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "setReadOnly")).build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

/**
 * Sets the type of the room
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param type The new type of the room.
 *
 * @return Whether the task was successful or not.
 */
suspend fun RocketChatClient.setType(
    roomId: String,
    roomType: RoomType,
    type: String
) = withContext(Dispatchers.IO) {
    val payload = ChatRoomTypePayload(roomId, type)
    val adapter = moshi.adapter(ChatRoomTypePayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "setType")).build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

/**
 * Sets the join code for a chat room
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param joinCode The new join code of the room.
 *
 * @return Whether the task was successful or not.
 */
suspend fun RocketChatClient.setJoinCode(
    roomId: String,
    roomType: RoomType,
    joinCode: String
) = withContext(Dispatchers.IO) {
    val payload = ChatRoomJoinCodePayload(roomId, joinCode)
    val adapter = moshi.adapter(ChatRoomJoinCodePayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "setJoinCode")).build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

/**
 * Sets a new topic for a chat room
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param topic The new topic of the room.
 *
 * @return Whether the task was successful or not.
 */
suspend fun RocketChatClient.setTopic(
    roomId: String,
    roomType: RoomType,
    topic: String?
): Boolean = withContext(Dispatchers.IO) {
    val payload = ChatRoomTopicPayload(roomId, topic)
    val adapter = moshi.adapter(ChatRoomTopicPayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "setTopic")).build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

/**
 * Sets a description for a chat room
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param description The new description of the room.
 *
 * @return Whether the task was successful or not.
 */
suspend fun RocketChatClient.setDescription(
    roomId: String,
    roomType: RoomType,
    description: String?
): Boolean = withContext(Dispatchers.IO) {
    val payload = ChatRoomDescriptionPayload(roomId, description)
    val adapter = moshi.adapter(ChatRoomDescriptionPayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "setDescription")).build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

/**
 * Sets an announcement for a chat room
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param announcement The new announcement of the room.
 *
 * @return Whether the task was successful or not.
 */
suspend fun RocketChatClient.setAnnouncement(
    roomId: String,
    roomType: RoomType,
    announcement: String?
): Boolean = withContext(Dispatchers.IO) {
    val payload = ChatRoomAnnouncementPayload(roomId, announcement)
    val adapter = moshi.adapter(ChatRoomAnnouncementPayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "setAnnouncement")).build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

/**
 * Archives a chat room
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param archiveRoom The archived status of the room.
 *
 * @return Whether the task was successful or not.
 */
suspend fun RocketChatClient.archive(
    roomId: String,
    roomType: RoomType,
    archiveRoom: Boolean
) = withContext(Dispatchers.IO) {
    val payload = RoomIdPayload(roomId)
    val adapter = moshi.adapter(RoomIdPayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val method: String = if (archiveRoom) "archive" else "unarchive"
    val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, method)).build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

/**
 * Shows or hides a chat room
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 * @param hideRoom The hidden status of the room (default: true).
 *
 * @return Whether the task was successful or not.
 */
suspend fun RocketChatClient.hide(
    roomId: String,
    roomType: RoomType,
    hideRoom: Boolean = true
) = withContext(Dispatchers.IO) {
    val payload = RoomIdPayload(roomId)
    val adapter = moshi.adapter(RoomIdPayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val method: String = if (hideRoom) "close" else "open"
    val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, method)).build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

/**
 * Shows a chat room
 *
 * @param roomId The ID of the room.
 * @param roomType The type of the room.
 *
 * @return Whether the task was successful or not.
 */
suspend fun RocketChatClient.show(
    roomId: String,
    roomType: RoomType
): Boolean = hide(roomId, roomType, false)

/**
 * Favorites or unfavorites a chat room.
 *
 * @param roomId The ID of the room.
 * @param favorite The value to favorite(true)/unfavorite(false) the chat room.
 *
 * @return Whether the task was successful or not.
 */
suspend fun RocketChatClient.favorite(
    roomId: String,
    favorite: Boolean
) = withContext(Dispatchers.IO) {
    val payload = ChatRoomFavoritePayload(roomId, favorite)
    val adapter = moshi.adapter(ChatRoomFavoritePayload::class.java)
    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "rooms.favorite").build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

/**
 * Search for messages in a channel by id and text message.
 *
 * @param roomId The ID of the room.
 * @param searchText The text message to search in messages.
 * @return The list of messages that satisfy the [searchText] term.
 */
suspend fun RocketChatClient.searchMessages(
    roomId: String,
    searchText: String
): PagedResult<List<Message>> = withContext(Dispatchers.IO) {
    val httpUrl = requestUrl(restUrl, "chat.search")
        .addQueryParameter("roomId", roomId)
        .addQueryParameter("searchText", searchText)
        .build()

    val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()

    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, Message::class.java)
    )

    val result = handleRestCall<RestResult<List<Message>>>(request, type)
    return@withContext PagedResult<List<Message>>(result.result(), result.total() ?: 0, result.offset() ?: 0)
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
): List<ChatRoomRole> = withContext(Dispatchers.IO) {

    val httpUrl = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "roles"))
        .addQueryParameter("roomName", roomName)
        .build()

    val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()

    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, ChatRoomRole::class.java)
    )
    return@withContext handleRestCall<RestResult<List<ChatRoomRole>>>(request, type).result()
}