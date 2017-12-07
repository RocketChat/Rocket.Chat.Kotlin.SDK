package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.common.util.CalendarISO8601Converter
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestMultiResult
import chat.rocket.core.internal.model.Subscription
import chat.rocket.core.model.ChatRoom
import chat.rocket.core.model.Myself
import chat.rocket.core.model.Room
import com.squareup.moshi.Types
import kotlinx.coroutines.experimental.async

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