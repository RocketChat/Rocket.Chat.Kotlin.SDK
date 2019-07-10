package chat.rocket.core.internal.realtime

import chat.rocket.common.RocketChatAuthException
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.realtime.message.createDirectMessage
import chat.rocket.core.internal.realtime.message.roomsStreamMessage
import chat.rocket.core.internal.realtime.message.streamRoomMessages
import chat.rocket.core.internal.realtime.message.streamTypingMessage
import chat.rocket.core.internal.realtime.message.typingMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun RocketChatClient.setTypingStatus(roomId: String, username: String, isTyping: Boolean) =
    withContext(Dispatchers.IO) {
        socket.send(typingMessage(socket.generateId(), roomId, username, isTyping))
    }

fun RocketChatClient.subscribeTypingStatus(roomId: String, callback: (Boolean, String) -> Unit): String {
    with(socket) {
        val id = generateId()
        send(streamTypingMessage(id, roomId))
        subscriptionsMap[id] = callback
        return id
    }
}

fun RocketChatClient.subscribeRooms(callback: (Boolean, String) -> Unit): String {
    with(socket) {
        client.tokenRepository.get(client.url)?.let { (userId) ->
            val id = generateId()
            send(roomsStreamMessage(id, userId))
            subscriptionsMap[id] = callback
            return id
        }
        throw RocketChatAuthException("Missing user id and token")
    }
}

fun RocketChatClient.subscribeRoomMessages(roomId: String, callback: (Boolean, String) -> Unit): String {
    with(socket) {
        val id = generateId()
        send(streamRoomMessages(id, roomId))
        subscriptionsMap[id] = callback
        return id
    }
}

fun RocketChatClient.createDirectMessage(username: String, callback: (Boolean, String) -> Unit): String {
    with(socket) {
        val id = generateId()
        send(createDirectMessage(id, username))
        subscriptionsMap[id] = callback
        return id
    }
}
