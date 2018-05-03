package chat.rocket.core.internal.realtime.message

/**
 * Streams are the way to plug into a continuous source of updates (changes). Any subscriber registered will receive
 * the latest changes as they occur.
 * In order to subscribe to a stream you must send a message with msg: sub, an unique id, the stream name and the params
 * to be applied on the stream.
 *
 * See https://rocket.chat/docs/developer-guides/realtime-api/subscriptions/
 */

private fun newSubscriptionMessage(name: String, id: String, params: String): String =
    "{\"msg\":\"sub\",\"id\":\"$id\",\"name\":\"$name\",\"params\":[$params]}"

internal fun unsubscribeMessage(id: String): String =
    "{\"msg\":\"unsub\", \"id\":\"$id\"}"

internal fun subscriptionsStreamMessage(id: String, userId: String): String =
    newSubscriptionMessage("stream-notify-user", id, "\"$userId/subscriptions-changed\", false")

internal fun roomsStreamMessage(id: String, userId: String): String =
    newSubscriptionMessage("stream-notify-user", id, "\"$userId/rooms-changed\", false")

internal fun streamRoomMessages(id: String, roomId: String): String =
    newSubscriptionMessage("stream-room-messages", id, "\"$roomId\", false")

internal fun userDataChangesMessage(id: String): String =
    newSubscriptionMessage("userData", id, "")

internal fun activeUsersMessage(id: String): String =
    newSubscriptionMessage("activeUsers", id, "")

internal fun streamTypingMessage(id: String, roomId: String): String =
    newSubscriptionMessage("stream-notify-room", id, "\"$roomId/typing\", false")