package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.common.model.User
import chat.rocket.common.util.CalendarISO8601Converter
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestMultiResult
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.*
import chat.rocket.core.model.ChatRoom
import chat.rocket.core.model.Myself
import chat.rocket.core.model.Room
import com.squareup.moshi.Types
import kotlinx.coroutines.experimental.async
import okhttp3.RequestBody

/**
 * Returns the current logged user information, useful to check if the Token from TokenProvider
 * is still valid.
 *
 * @see Myself
 * @see RocketChatException
 */
suspend fun RocketChatClient.me(): Myself {
    val httpUrl = requestUrl(restUrl, "me").build()
    val request = requestBuilder(httpUrl).get().build()

    return handleRestCall(request, Myself::class.java)
}

/**
 * Updates the email address for the user.
 *
 * @param userId The ID of the user to update.
 * @param email The email address for the user.
 * @return The [User] with an updated email address.
 */
suspend fun RocketChatClient.updateEmail(userId: String, email: String): User {
    val payload = UserUpdateEmailPayload(userId, email)
    val adapter = moshi.adapter(UserUpdateEmailPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(JSON_CONTENT_TYPE, payloadBody)

    val httpUrl = requestUrl(restUrl, "users.update").build()
    val request = requestBuilder(httpUrl).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, User::class.java)
    return handleRestCall<RestResult<User>>(request, type).result()
}

/**
 * Updates the display name of the user.
 *
 * @param userId The ID of the user to update.
 * @param name The display name of the user.
 * @return The [User] with an updated display name.
 */
suspend fun RocketChatClient.updateName(userId: String, name: String): User {
    val payload = UserUpdateNamePayload(userId, name)
    val adapter = moshi.adapter(UserUpdateNamePayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(JSON_CONTENT_TYPE, payloadBody)

    val httpUrl = requestUrl(restUrl, "users.update").build()
    val request = requestBuilder(httpUrl).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, User::class.java)
    return handleRestCall<RestResult<User>>(request, type).result()
}

/**
 * Updates the password for the user.
 *
 * @param userId The ID of the user to update.
 * @param password The password for the user.
 * @return The [User] with an updated password.
 */
suspend fun RocketChatClient.updatePassword(userId: String, password: String): User {
    val payload = UserUpdatePasswordPayload(userId, password)
    val adapter = moshi.adapter(UserUpdatePasswordPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(JSON_CONTENT_TYPE, payloadBody)

    val httpUrl = requestUrl(restUrl, "users.update").build()
    val request = requestBuilder(httpUrl).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, User::class.java)
    return handleRestCall<RestResult<User>>(request, type).result()
}

/**
 * Updates the username for the user.
 *
 * @param userId The ID of the user to update.
 * @param username The username for the user.
 * @return The [User] with an updated username.
 */
suspend fun RocketChatClient.updateUsername(userId: String, username: String): User {
    val payload = UserUpdateUsernamePayload(userId, username)
    val adapter = moshi.adapter(UserUpdateUsernamePayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(JSON_CONTENT_TYPE, payloadBody)

    val httpUrl = requestUrl(restUrl, "users.update").build()
    val request = requestBuilder(httpUrl).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, User::class.java)
    return handleRestCall<RestResult<User>>(request, type).result()
}

suspend fun RocketChatClient.chatRooms(timestamp: Long = 0): RestMultiResult<List<ChatRoom>> {
    val rooms = async { listRooms(timestamp) }
    val subscriptions = async { listSubscriptions(timestamp) }

    return combine(rooms.await(), subscriptions.await())
}

internal fun RocketChatClient.combine(rooms: RestMultiResult<List<Room>>, subscriptions: RestMultiResult<List<Subscription>>): RestMultiResult<List<ChatRoom>> {
    val update = combine(rooms.update, subscriptions.update)
    val remove = combine(rooms.remove, subscriptions.remove)

    return RestMultiResult.create(update, remove)
}

internal fun RocketChatClient.combine(rooms: List<Room>, subscriptions: List<Subscription>): List<ChatRoom> {
    val map = HashMap<String, Room>()
    rooms.forEach {
        map[it.id] = it
    }

    val chatRooms = ArrayList<ChatRoom>(subscriptions.size)

    subscriptions.forEach {
        val room = map[it.roomId]
        val subscription = it
        // In case of any inconsistency we just ignore the room/subscription...
        // This should be a very, very rare situation, like the user leaving/joining a channel
        // between the 2 calls.
        room?.let {
            chatRooms.add(ChatRoom.create(room, subscription, this))
        }
    }

    return chatRooms
}

internal suspend fun RocketChatClient.listSubscriptions(timestamp: Long = 0): RestMultiResult<List<Subscription>> {
    val urlBuilder = requestUrl(restUrl, "subscriptions.get")
    val date = CalendarISO8601Converter().fromTimestamp(timestamp)
    urlBuilder.addQueryParameter("updatedAt", date)

    val request = requestBuilder(urlBuilder.build()).get().build()

    val type = Types.newParameterizedType(RestMultiResult::class.java,
            Types.newParameterizedType(List::class.java, Subscription::class.java))
    val result = handleRestCall<RestMultiResult<List<Subscription>>>(request, type)
    return result
}

internal suspend fun RocketChatClient.listRooms(timestamp: Long = 0): RestMultiResult<List<Room>> {
    val urlBuilder = requestUrl(restUrl, "rooms.get")
    val date = CalendarISO8601Converter().fromTimestamp(timestamp)
    urlBuilder.addQueryParameter("updatedAt", date)

    val request = requestBuilder(urlBuilder.build()).get().build()

    val type = Types.newParameterizedType(RestMultiResult::class.java,
            Types.newParameterizedType(List::class.java, Room::class.java))
    val result = handleRestCall<RestMultiResult<List<Room>>>(request, type)
    return result
}