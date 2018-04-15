package chat.rocket.core.internal.realtime.socket

import chat.rocket.core.internal.realtime.socket.message.collection.STREAM_NOTIFY_USER
import chat.rocket.core.internal.realtime.socket.message.collection.STREAM_ROOM_MESSAGES
import chat.rocket.core.internal.realtime.socket.message.collection.USERS
import chat.rocket.core.internal.realtime.socket.message.collection.processUserStream
import chat.rocket.core.internal.realtime.socket.message.collection.processRoomMessage
import chat.rocket.core.internal.realtime.socket.message.collection.processUsersStream
import chat.rocket.core.internal.realtime.socket.message.model.SocketMessage
import org.json.JSONObject

internal fun Socket.processSubscriptionsChanged(message: SocketMessage, text: String) {
    when (message.collection) {
        STREAM_NOTIFY_USER -> {
            processUserStream(text)
        }
        STREAM_ROOM_MESSAGES -> {
            processRoomMessage(text)
        }
        USERS -> {
            processUsersStream(text)
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