package chat.rocket.core.internal.realtime

import chat.rocket.common.RocketChatAuthException
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.realtime.message.roomsStreamMessage
import chat.rocket.core.internal.realtime.message.streamRoomMessages

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