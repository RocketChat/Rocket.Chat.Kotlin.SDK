package chat.rocket.core.internal.realtime.socket

import chat.rocket.core.internal.realtime.socket.message.collection.STREAM_NOTIFY_ROOM
import chat.rocket.core.internal.realtime.socket.message.collection.STREAM_NOTIFY_USER
import chat.rocket.core.internal.realtime.socket.message.collection.STREAM_ROOM_MESSAGES
import chat.rocket.core.internal.realtime.socket.message.collection.USERS
import chat.rocket.core.internal.realtime.socket.message.collection.processNotifyRoomStream
import chat.rocket.core.internal.realtime.socket.message.collection.processNotifyUserStream
import chat.rocket.core.internal.realtime.socket.message.collection.processRoomMessage
import chat.rocket.core.internal.realtime.socket.message.collection.processUserStream
import chat.rocket.core.internal.realtime.socket.message.model.SocketMessage
import org.json.JSONObject

internal fun Socket.processSubscriptionsAdded(message: SocketMessage, text: String) {
    when (message.collection) {
        USERS -> {
            processUserStream(text)
        }
        else -> {
            // IGNORE for now
        }
    }
}

internal fun Socket.processSubscriptionsRemoved(message: SocketMessage, text: String) {
    when (message.collection) {
        USERS -> {
            processUserStream(text)
        }
        else -> {
            // IGNORE for now
        }
    }
}

internal fun Socket.processSubscriptionsChanged(message: SocketMessage, text: String) {
    when (message.collection) {
        STREAM_NOTIFY_USER -> {
            processNotifyUserStream(text)
        }
        STREAM_ROOM_MESSAGES -> {
            processRoomMessage(text)
        }
        STREAM_NOTIFY_ROOM -> {
            processNotifyRoomStream(text)
        }
        USERS -> {
            processUserStream(text)
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