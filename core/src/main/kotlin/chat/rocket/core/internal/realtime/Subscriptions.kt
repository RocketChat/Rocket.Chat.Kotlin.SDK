package chat.rocket.core.internal.realtime

import chat.rocket.common.RocketChatAuthException
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.model.SocketMessage
import chat.rocket.core.internal.model.Subscription
import chat.rocket.core.model.Message
import chat.rocket.core.model.Room
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject
import java.security.InvalidParameterException

fun RocketChatClient.subscribeSubscriptions(callback: (Boolean, String) -> Unit): String {
    return socket.subscribeSubscriptions(callback)
}

fun RocketChatClient.subscribeRooms(callback: (Boolean, String) -> Unit): String {
    return socket.subscribeRooms(callback)
}

fun RocketChatClient.subscribeRoomMessages(roomId: String, callback: (Boolean, String) -> Unit): String {
    return socket.subscribeRoomMessages(roomId, callback)
}

fun RocketChatClient.unsubscribe(subId: String) {
    socket.unsubscribe(subId)
}

internal fun Socket.subscribeSubscriptions(callback: (Boolean, String) -> Unit): String {
    client.tokenRepository.get()?.let { (userId) ->
        val id = generateId()
        send(subscriptionsStreamMessage(id, userId))
        subscriptionsMap[id] = callback
        return id
    }

    throw RocketChatAuthException("Missing user id and token")
}

internal fun Socket.subscribeRooms(callback: (Boolean, String) -> Unit): String {
    client.tokenRepository.get()?.let { (userId) ->
        val id = generateId()
        send(roomsStreamMessage(id, userId))
        subscriptionsMap[id] = callback
        return id
    }

    throw RocketChatAuthException("Missing user id and token")
}

internal fun Socket.subscribeRoomMessages(roomId: String, callback: (Boolean, String) -> Unit): String {
    val id = generateId()
    send(streamRoomMessages(id, roomId))
    subscriptionsMap[id] = callback
    return id
}

internal fun Socket.unsubscribe(subId: String) {
    send(unsubscribeMessage(subId))
}

fun Socket.processSubscriptionsChanged(message: SocketMessage, text: String) {
    when (message.collection) {
        "stream-notify-user" -> {
            processUserStream(text)
        }
        "stream-room-messages" -> {
            processRoomMessage(text)
        }
        else -> {
            // IGNORE for now
        }
    }
}

internal fun Socket.processSubscriptionResult(message: String) {
    val subId: String
    try {
        val json = JSONObject(message)
        val array = json.getJSONArray("subs")
        subId = array.getString(0)
    } catch (ex: Exception) {
        ex.printStackTrace()
        return
    }

    val callback = subscriptionsMap.remove(subId)
    callback?.let {
        callback(true, subId)
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

private fun Socket.processRoomMessage(text: String) {
    try {
        val json = JSONObject(text)
        val fields = json.getJSONObject("fields")
        val array = fields.getJSONArray("args")
        val data = array.getJSONObject(0)
        val adapter = moshi.adapter<Message>(Message::class.java)
        val message = adapter.fromJson(data.toString())

        message?.let {
            launch(parent = parentJob) {
                messagesChannel.send(message)
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
        launch(parent = parentJob) {
            roomsChannel.send(StreamMessage(getMessageType(state), room))
        }
    }
}

private fun Socket.processSubscriptionStream(state: String, data: JSONObject) {
    val adapter = moshi.adapter<Subscription>(Subscription::class.java)
    val subscription = adapter.fromJson(data.toString())

    subscription?.apply {
        launch(parent = parentJob) {
            subscriptionsChannel.send(StreamMessage(getMessageType(state), subscription))
        }
    }
}

private fun getMessageType(state: String): Type {
    return when (state) {
        "inserted", "added" -> {
            Type.Inserted
        }
        "updated", "changed" -> {
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
