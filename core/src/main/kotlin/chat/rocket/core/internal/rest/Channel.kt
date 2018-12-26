package chat.rocket.core.internal.rest

import chat.rocket.common.model.RoomType
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.CreateDirectMessagePayload
import chat.rocket.core.internal.model.CreateNewChannelPayload
import chat.rocket.core.model.DirectMessage
import chat.rocket.core.model.Room
import com.squareup.moshi.Types
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
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
): Room = withContext(CommonPool) {
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
    withContext(CommonPool) {
        val payload = CreateDirectMessagePayload(username = username)
        val adapter = moshi.adapter(CreateDirectMessagePayload::class.java)
        val payloadBody = adapter.toJson(payload)

        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, "im.create").build()
        val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

        val type = Types.newParameterizedType(RestResult::class.java, DirectMessage::class.java)

        return@withContext handleRestCall<RestResult<DirectMessage>>(request, type).result()
    }