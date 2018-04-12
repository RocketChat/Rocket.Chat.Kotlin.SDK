package chat.rocket.core.internal.realtime.socket.message.collection

import chat.rocket.common.model.User
import chat.rocket.core.internal.realtime.socket.Socket
import chat.rocket.core.model.Myself
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject

internal const val USERS = "users"

internal fun Socket.processUserStream(text: String) {
    try {
        val json = JSONObject(text)
        val id = json.optString("id")

        client.tokenRepository.get(client.url)?.let { (userId) ->
            if (id == userId) {
                processUserDataStream(json, id)
            } else {
                processActiveUsersStream(json, id)
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

private fun Socket.processUserDataStream(json: JSONObject, userId: String) {
    val fields = json.optJSONObject("fields")
    fields.put("_id", userId)

    val adapter = moshi.adapter<Myself>(Myself::class.java)
    val myself = adapter.fromJson(fields.toString())
    myself?.let {
        launch(parent = parentJob) {
            userDataChannel.send(myself)
        }
    }
}

private fun Socket.processActiveUsersStream(json: JSONObject, id: String) {
    var fields = json.optJSONObject("fields")
    if (fields == null) {
        fields = JSONObject()
    }
    fields.put("_id", id)

    if (json.optString("msg") == "removed") {
        fields.put("status", "offline")
    }

    val adapter = moshi.adapter<User>(User::class.java)
    val user = adapter.fromJson(fields.toString())
    user?.let {
        launch(parent = parentJob) {
            activeUsersChannel.send(user)
        }
    }
}