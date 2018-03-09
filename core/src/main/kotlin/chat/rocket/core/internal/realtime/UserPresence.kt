package chat.rocket.core.internal.realtime

import chat.rocket.core.RocketChatClient
import com.squareup.moshi.Json
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext

suspend fun RocketChatClient.setDefaultStatus(status: UserStatus) = withContext(CommonPool) {
    // Should we track the response???
    socket.send(defaultStatusMessage(socket.generateId(), status))
}

suspend fun RocketChatClient.setConnectionStatus(status: UserStatus) = withContext(CommonPool) {
    // Should we track the response???
    if (status is UserStatus.Online || status is UserStatus.Away) {
        socket.send(connectionStatusMessage(socket.generateId(), status))
    } else {
        logger.warn { "Only \"away\" and \"online\" are accepted as connection status" }
    }
}

sealed class UserStatus {
    @Json(name = "online") object Online : UserStatus()
    @Json(name = "busy") object Busy : UserStatus()
    @Json(name = "away") object Away : UserStatus()
    @Json(name = "offline") object Offline : UserStatus()

    override fun toString(): String {
        return when(this) {
            is Online -> "online"
            is Busy -> "busy"
            is Away -> "away"
            is Offline -> "offline"
        }
    }
}