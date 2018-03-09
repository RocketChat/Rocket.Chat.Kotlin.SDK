package chat.rocket.core.internal.realtime

import chat.rocket.core.RocketChatClient
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext

enum class UserStatus(val value: String) {
    ONLINE("online"),
    BUSY("busy"),
    AWAY("away"),
    OFFLINE("offline")
}

suspend fun RocketChatClient.setDefaultStatus(status: UserStatus) = withContext(CommonPool) {
    socket.send(defaultStatusMessage(socket.generateId(), status))
}

suspend fun RocketChatClient.setConnectionStatus(status: UserStatus) = withContext(CommonPool) {
    if (status == UserStatus.ONLINE || status == UserStatus.AWAY) {
        socket.send(connectionStatusMessage(socket.generateId(), status))
    } else {
        logger.warn { "Only \"away\" and \"online\" are accepted as connection status" }
    }
}