package chat.rocket.core.internal.realtime.socket

import chat.rocket.common.model.Token
import chat.rocket.core.internal.realtime.socket.model.SocketToken
import chat.rocket.core.internal.realtime.socket.model.State
import chat.rocket.core.internal.model.TypedResponse
import chat.rocket.core.internal.realtime.message.loginMethod
import com.squareup.moshi.Types
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
fun Socket.login(token: Token?) {
    token?.let { authToken ->
        socket?.let {
            setState(State.Authenticating())
            send(loginMethod(generateId(), authToken.authToken))
        }
    }
}

@ObsoleteCoroutinesApi
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