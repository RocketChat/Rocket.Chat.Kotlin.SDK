package chat.rocket.core.internal.realtime

import chat.rocket.core.internal.model.SocketToken
import chat.rocket.core.internal.model.TypedResponse
import com.squareup.moshi.Types

internal fun Socket.processLoginResult(text: String) {
    val type = Types.newParameterizedType(TypedResponse::class.java, SocketToken::class.java)
    val adapter = moshi.adapter<TypedResponse<SocketToken>>(type)

    try {
        val token = adapter.fromJson(text)
        logger.debug {
            "Logged in: $token"
        }
        setState(State.Connected())
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}