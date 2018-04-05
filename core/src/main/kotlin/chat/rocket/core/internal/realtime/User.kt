package chat.rocket.core.internal.realtime

import chat.rocket.core.RocketChatClient
import com.squareup.moshi.Json
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext

suspend fun RocketChatClient.setDefaultStatus(status: UserStatus) = withContext(CommonPool) {
    socket.send(defaultStatusMessage(socket.generateId(), status))
}

suspend fun RocketChatClient.setTemporaryStatus(status: UserStatus) = withContext(CommonPool) {
    when {
        (status == UserStatus.Online || status == UserStatus.Away) -> {
            socket.send(temporaryStatusMessage(socket.generateId(), status))
        }
        else -> {
            logger.warn { "Only \"UserStatus.Online\" and \"UserStatus.Away\" are accepted as temporary status" }
        }
    }
}

suspend fun RocketChatClient.getUserDataChanges() = withContext(CommonPool) {
    socket.send(userDataChangesMessage(socket.generateId()))
}

sealed class UserStatus {
    @Json(name = "online") object Online : UserStatus()
    @Json(name = "busy") object Busy : UserStatus()
    @Json(name = "away") object Away : UserStatus()
    @Json(name = "offline") object Offline : UserStatus()

    override fun toString(): String {
        return when (this) {
            is Online -> "online"
            is Busy -> "busy"
            is Away -> "away"
            is Offline -> "offline"
        }
    }
}