package chat.rocket.core.internal.realtime

import chat.rocket.common.RocketChatAuthException
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.realtime.message.subscriptionsStreamMessage
import chat.rocket.core.internal.realtime.message.unsubscribeMessage

fun RocketChatClient.subscribeSubscriptions(callback: (Boolean, String) -> Unit): String {
    with(socket) {
        client.tokenRepository.get(client.url)?.let { (userId) ->
            val id = generateId()
            send(subscriptionsStreamMessage(id, userId))
            subscriptionsMap[id] = callback
            return id
        }

        throw RocketChatAuthException("Missing user id and token")
    }
}

fun RocketChatClient.unsubscribe(subId: String) {
    socket.send(unsubscribeMessage(subId))
}