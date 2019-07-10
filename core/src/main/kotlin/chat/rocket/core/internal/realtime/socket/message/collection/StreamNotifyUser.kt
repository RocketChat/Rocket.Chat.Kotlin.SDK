package chat.rocket.core.internal.realtime.socket.message.collection

import chat.rocket.core.internal.model.Subscription
import chat.rocket.core.internal.realtime.socket.Socket
import chat.rocket.core.internal.realtime.socket.model.StreamMessage
import chat.rocket.core.internal.realtime.socket.model.Type
import chat.rocket.core.model.Room
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.security.InvalidParameterException

internal const val STREAM_NOTIFY_USER = "stream-notify-user"
private const val STREAM_ROOMS_CHANGED = "rooms-changed"
private const val STREAM_SUBSCRIPTION_CHANGED = "subscriptions-changed"

internal fun Socket.processNotifyUserStream(text: String) {
    try {
        val json = JSONObject(text)
        val fields = json.getJSONObject("fields")
        val stream = getStreamType(fields.getString("eventName"))
        val array = fields.getJSONArray("args")
        val state = array.getString(0)
        val data = array.getJSONObject(1)

        when (stream) {
            STREAM_ROOMS_CHANGED -> {
                processRoomStream(state, data)
            }
            STREAM_SUBSCRIPTION_CHANGED -> {
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
        if (parentJob == null || !parentJob!!.isActive) {
            logger.debug { "Parent job: $parentJob" }
        }
        launch(parentJob) {
            if (roomsChannel.isFull || roomsChannel.isClosedForSend) {
                logger.debug { "Rooms channel is in trouble... $roomsChannel - full ${roomsChannel.isFull} - closedForSend ${roomsChannel.isClosedForSend}" }
            }
            roomsChannel.send(StreamMessage(getMessageType(state), room))
        }
    }
}

private fun Socket.processSubscriptionStream(state: String, data: JSONObject) {
    val adapter = moshi.adapter<Subscription>(Subscription::class.java)
    val subscription = adapter.fromJson(data.toString())?.let { sub ->
        // Filter subscriptions that don't have both name and fname
        if (sub.name == null && sub.fullName == null) {
            null
        } else if (sub.name == null && sub.fullName != null) {
            sub.copy(name = sub.fullName)
        } else {
            sub
        }
    }

    subscription?.apply {
        launch(parentJob) { subscriptionsChannel.send(StreamMessage(getMessageType(state), subscription)) }
    }
}

private fun getStreamType(eventName: String): String {
    return eventName.split("/")[1]
}

private fun getMessageType(state: String): Type {
    return when (state) {
        "inserted", "added" -> {
            Type.Inserted
        }
        "updated", "changed" -> {
            Type.Updated
        }
        "removed" -> {
            Type.Removed
        }
        else -> {
            throw InvalidParameterException("Unknown type: $state")
        }
    }
}