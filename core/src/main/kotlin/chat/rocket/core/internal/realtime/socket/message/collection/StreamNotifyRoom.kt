package chat.rocket.core.internal.realtime.socket.message.collection

import chat.rocket.core.internal.realtime.socket.Socket
import kotlinx.coroutines.launch
import org.json.JSONObject

internal const val STREAM_NOTIFY_ROOM = "stream-notify-room"

internal fun Socket.processNotifyRoomStream(text: String) {
    try {
        val json = JSONObject(text)
        val fields = json.getJSONObject("fields")
        val array = fields.getJSONArray("args")

        launch(parentJob) { typingStatusChannel.send(Pair(array.getString(0), array.getBoolean(1))) }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}