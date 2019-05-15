package chat.rocket.core.internal.realtime.socket.message.collection

import chat.rocket.core.internal.realtime.socket.Socket
import chat.rocket.core.model.Message
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import org.json.JSONObject

internal const val STREAM_ROOM_MESSAGES = "stream-room-messages"

@ObsoleteCoroutinesApi
internal fun Socket.processRoomMessage(text: String) {
    try {
        val json = JSONObject(text)
        val fields = json.getJSONObject("fields")
        val array = fields.getJSONArray("args")
        val data = array.getJSONObject(0)
        val adapter = moshi.adapter<Message>(Message::class.java)
        val message = adapter.fromJson(data.toString())

        message?.let {
            launch(parentJob) { messagesChannel.send(message) }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}