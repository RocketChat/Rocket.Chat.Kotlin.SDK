package chat.rocket.core.internal.realtime.socket.message.collection

import chat.rocket.core.internal.realtime.socket.Socket
import chat.rocket.core.model.Myself
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject

internal const val USERS = "users"

internal fun Socket.processUsersStream(text: String) {
    try {
        val json = JSONObject(text)
        val fields = json.getJSONObject("fields")
        val adapter = moshi.adapter<Myself>(Myself::class.java)
        val myself = adapter.fromJson(fields.toString())

        myself?.let {
            launch(parent = parentJob) {
                userDataChannel.send(myself)
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}