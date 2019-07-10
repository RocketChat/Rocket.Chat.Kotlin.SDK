package chat.rocket.core.internal.rest

import chat.rocket.common.model.RoomType
import chat.rocket.common.model.BaseResult
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.CreateDirectMessagePayload
import chat.rocket.core.internal.model.CreateNewChannelPayload
import chat.rocket.core.model.DirectMessage
import chat.rocket.core.internal.model.ChatRoomUnreadPayload
import chat.rocket.core.internal.model.ChatRoomUserPayload
import chat.rocket.core.internal.model.ChatRoomIdUserPayload
import chat.rocket.core.internal.model.ChatRoomUserIgnorePayload
import chat.rocket.core.model.Room
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody

/**
 * Creates a new chat room.
 *
 * @param roomType The type of the room.
 * @param name Name of the chat room
 * @param usersList The list of users who are invited to join the chat room.
 * @param readOnly Tells whether to keep the new chat room read only or not.
 */
suspend fun RocketChatClient.createChannel(
    roomType: RoomType,
    name: String,
    usersList: List<String>?,
    readOnly: Boolean? = false
): Room = withContext(Dispatchers.IO) {
    val payload = CreateNewChannelPayload(name, usersList, readOnly)
    val adapter = moshi.adapter(CreateNewChannelPayload::class.java)
    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "create")).build()

    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()
    val type = Types.newParameterizedType(RestResult::class.java, Room::class.java)

    return@withContext handleRestCall<RestResult<Room>>(request, type).result()
}

/**
 * Create a direct message (DM) room with an user.
 *
 * @param username The username of the user to create a DM with.
 * @return A DirectMessage object.
 */
suspend fun RocketChatClient.createDirectMessage(username: String): DirectMessage =
    withContext(Dispatchers.IO) {
        val payload = CreateDirectMessagePayload(username = username)
        val adapter = moshi.adapter(CreateDirectMessagePayload::class.java)
        val payloadBody = adapter.toJson(payload)

        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, "im.create").build()
        val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

        val type = Types.newParameterizedType(RestResult::class.java, DirectMessage::class.java)

        return@withContext handleRestCall<RestResult<DirectMessage>>(request, type).result()
    }
 /* Add owner of a Channel/Group
 * @param roomId Id of the room.
 * @param roomType The type of the room.
 */
suspend fun RocketChatClient.addOwner(roomId: String ,roomType: RoomType, userId: String) { 
    withContext(CommonPool) {
        val payload = ChatRoomUserPayload(roomId, userId)
        val adapter = moshi.adapter(ChatRoomUserPayload::class.java)
        val payloadBody = adapter.toJson(payload)
        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "addOwner")).build()

        val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}

/**
 * Add leader of a Channel/Group
 * @param roomId Id of the room.
 * @param roomType The type of the room.
 */
suspend fun RocketChatClient.addLeader(roomId: String ,roomType: RoomType, userId: String) { 
    withContext(CommonPool) {
        val payload = ChatRoomUserPayload(roomId, userId)
        val adapter = moshi.adapter(ChatRoomUserPayload::class.java)
        val payloadBody = adapter.toJson(payload)
        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "addLeader")).build()

        val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}

/**
 * Add Moderator of a Channel/Group
 * @param roomId Id of the room.
 * @param roomType The type of the room.
 */
suspend fun RocketChatClient.addModerator(roomId: String ,roomType: RoomType, userId: String) { 
    withContext(CommonPool) {
        val payload = ChatRoomUserPayload(roomId, userId)
        val adapter = moshi.adapter(ChatRoomUserPayload::class.java)
        val payloadBody = adapter.toJson(payload)
        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "addModerator")).build()

        val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}

/**
 * Remove Owner of a Channel/Group
 * @param roomId Id of the room.
 * @param roomType The type of the room.
 */
suspend fun RocketChatClient.removeOwner(roomId: String ,roomType: RoomType, userId: String) { 
    withContext(CommonPool) {
        val payload = ChatRoomUserPayload(roomId, userId)
        val adapter = moshi.adapter(ChatRoomUserPayload::class.java)
        val payloadBody = adapter.toJson(payload)
        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "removeOwner")).build()

        val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}

/**
 * Remove Leader of a Channel/Group
 * @param roomId Id of the room.
 * @param roomType The type of the room.
 */
suspend fun RocketChatClient.removeLeader(roomId: String ,roomType: RoomType, userId: String) { 
    withContext(CommonPool) {
        val payload = ChatRoomUserPayload(roomId, userId)
        val adapter = moshi.adapter(ChatRoomUserPayload::class.java)
        val payloadBody = adapter.toJson(payload)
        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "removeLeader")).build()

        val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}

/**
 * Remove Moderator of a Channel/Group
 * @param roomId Id of the room.
 * @param roomType The type of the room.
 */
suspend fun RocketChatClient.removeModerator(roomId: String ,roomType: RoomType, userId: String) { 
    withContext(CommonPool) {
        val payload = ChatRoomUserPayload(roomId, userId)
        val adapter = moshi.adapter(ChatRoomUserPayload::class.java)
        val payloadBody = adapter.toJson(payload)
        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "removeModerator")).build()

        val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}

/**
 * Remove User of a Channel/Group
 * @param roomId Id of the room.
 * @param roomType The type of the room.
 * @param userId Id of user to remove from room.
 */
suspend fun RocketChatClient.removeUser(roomId: String ,roomType: RoomType, userId: String) { 
    withContext(CommonPool) {
        val payload = ChatRoomUserPayload(roomId, userId)
        val adapter = moshi.adapter(ChatRoomUserPayload::class.java)
        val payloadBody = adapter.toJson(payload)
        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "kick")).build()

        val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}