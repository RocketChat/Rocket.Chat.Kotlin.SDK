package chat.rocket.core.internal.realtime

private fun newMessage(method: String, id: String, params: String): String =
    "{\"msg\":\"method\",\"id\":\"$id\",\"method\":\"$method\",\"params\":[$params]}"

private fun newSubscriptionMessage(name: String, id: String, params: String): String =
    "{\"msg\":\"sub\",\"id\":\"$id\",\"name\":\"$name\",\"params\":[$params]}"

internal fun loginMethod(id: String, token: String): String =
    newMessage("login", id, "{\"resume\":\"$token\"}")

internal fun pongMessage(): String =
    "{\"msg\":\"pong\"}"

internal fun pingMessage(): String =
    "{\"msg\":\"ping\"}"

internal fun subscriptionsStreamMessage(id: String, userId: String): String =
    newSubscriptionMessage("stream-notify-user", id, "\"$userId/subscriptions-changed\", false")

internal fun roomsStreamMessage(id: String, userId: String): String =
    newSubscriptionMessage("stream-notify-user", id, "\"$userId/rooms-changed\", false")

internal fun streamRoomMessages(id: String, roomId: String): String =
    newSubscriptionMessage("stream-room-messages", id, "\"$roomId\", false")

internal fun unsubscribeMessage(id: String): String =
    "{\"msg\":\"unsub\", \"id\":\"$id\"}"

internal fun defaultStatusMessage(id: String, status: UserStatus): String =
    newMessage("UserPresence:setDefaultStatus", id, "\"$status\"")

internal fun temporaryStatusMessage(id: String, status: UserStatus): String =
    newMessage("UserPresence:$status", id, "")

internal fun userDataChangesMessage(id: String): String =
    newSubscriptionMessage("userData", id, "")

internal const val CONNECT_MESSAGE = "{\"msg\":\"connect\",\"version\":\"1\",\"support\":[\"1\",\"pre2\",\"pre1\"]}"