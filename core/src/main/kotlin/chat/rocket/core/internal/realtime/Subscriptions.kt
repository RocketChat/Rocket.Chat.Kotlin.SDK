package chat.rocket.core.internal.realtime

import chat.rocket.common.RocketChatAuthException
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.model.SocketMessage
import chat.rocket.core.internal.model.Subscription
import chat.rocket.core.model.Room
import org.json.JSONObject
import java.security.InvalidParameterException

fun RocketChatClient.subscribeSubscriptions() {
    socket.subscribeSubscriptions()
}

fun RocketChatClient.subscribeRooms() {
    socket.subscribeRooms()
}

internal fun Socket.subscribeSubscriptions(): String {
    client.tokenRepository.get()?.let { token ->
        val id = generateId()
        send(subscriptionsStreamMessage(id, token.userId))
        return id
    }

    throw RocketChatAuthException("Missing user id and token")
}

internal fun Socket.subscribeRooms(): String {
    client.tokenRepository.get()?.let { token ->
        val id = generateId()
        send(roomsStreamMessage(id, token.userId))
        return id
    }

    throw RocketChatAuthException("Missing user id and token")
}

fun Socket.processSubscriptionsChanged(message: SocketMessage, text: String) {
    when (message.collection) {
        "stream-notify-user" -> {
            processUserStream(text)
        }
        else -> {
            // IGNORE for now
        }
    }
}

private fun Socket.processUserStream(text: String) {
    try {
        val json = JSONObject(text)
        val fields = json.getJSONObject("fields")
        val stream = getStreamType(fields.getString("eventName"))
        val array = fields.getJSONArray("args")
        val state = array.getString(0)
        val data = array.getJSONObject(1)

        when (stream) {
            "rooms-changed" -> {
                processRoomStream(state, data)
            }
            "subscriptions-changed" -> {
                processSubscriptionStream(state, data)
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

private fun Socket.processRoomStream(state: String, data: JSONObject) {
    val adapter = moshi.adapter<Room>(Room::class.java)
    val room = adapter.fromJson(data.toString())

    room?.apply {
        roomsChannel.offer(StreamMessage(getMessageType(state), room))
    }
}

private fun Socket.processSubscriptionStream(state: String, data: JSONObject) {
    val adapter = moshi.adapter<Subscription>(Subscription::class.java)
    val subscription = adapter.fromJson(data.toString())

    subscription?.apply {
        subscriptionsChannel.offer(StreamMessage(getMessageType(state), subscription))
    }
}

private fun getMessageType(state: String): Type {
    return when (state) {
        "inserted" -> {
            Type.Inserted
        }
        "updated" -> {
            Type.Updated
        }
        "removed" -> Type.Removed
        else -> {
            throw InvalidParameterException("Unknown type: $state")
        }
    }
}

private fun getStreamType(eventName: String): String {
    return eventName.split("/")[1]
}

sealed class Type {
    object Inserted : Type()
    object Updated : Type()
    object Removed : Type()
}

data class StreamMessage<out T>(val type: Type, val data: T)