package chat.rocket.core.internal.realtime

import chat.rocket.common.model.UserStatus
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.realtime.message.activeUsersMessage
import chat.rocket.core.internal.realtime.message.defaultStatusMessage
import chat.rocket.core.internal.realtime.message.temporaryStatusMessage
import chat.rocket.core.internal.realtime.message.userDataChangesMessage

fun RocketChatClient.setDefaultStatus(status: UserStatus) {
    socket.send(defaultStatusMessage(socket.generateId(), status))
}

fun RocketChatClient.setTemporaryStatus(status: UserStatus) {
    when {
        (status is UserStatus.Online || status is UserStatus.Away) -> {
            socket.send(temporaryStatusMessage(socket.generateId(), status))
        }
        else -> {
            logger.warn { "Only \"UserStatus.Online\" and \"UserStatus.Away\" are accepted as temporary status" }
        }
    }
}

fun RocketChatClient.subscribeUserData(callback: (Boolean, String) -> Unit): String {
    with(socket) {
        val id = generateId()
        send(userDataChangesMessage(id))
        subscriptionsMap[id] = callback
        return id
    }
}

fun RocketChatClient.subscribeActiveUsers(callback: (Boolean, String) -> Unit): String {
    with(socket) {
        val id = generateId()
        send(activeUsersMessage(id))
        subscriptionsMap[id] = callback
        return id
    }
}