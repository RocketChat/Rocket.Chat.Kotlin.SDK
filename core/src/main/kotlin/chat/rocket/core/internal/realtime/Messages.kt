package chat.rocket.core.internal.realtime

private fun newMessage(method: String, id: String, params: String): String {
    return "{\n" +
            "    \"msg\": \"method\",\n" +
            "    \"method\": \"$method\",\n" +
            "    \"id\":\"$id\",\n" +
            "    \"params\":[\n" +
            "          $params" +
            "    ]\n" +
            "}"
}

private fun newSubscriptionMessage(name: String, id: String, params: String): String {
    return "{\n" +
            "    \"msg\": \"sub\",\n" +
            "    \"id\": \"$id\",\n" +
            "    \"name\": \"$name\",\n" +
            "    \"params\":[\n" +
            "        $params" +
            "    ]\n" +
            "}"
}

internal fun loginMethod(id: String, token: String): String {
    return newMessage("login", id, "{\"resume\":\"$token\"}")
}

internal fun pongMessage(): String {
    return "{\"msg\":\"pong\"}"
}

internal fun pingMessage(): String {
    return "{\"msg\":\"ping\"}"
}

internal fun subscriptionsStreamMessage(id: String, userId: String): String {
    return newSubscriptionMessage("stream-notify-user", id,
            "\"$userId/subscriptions-changed\", false")
}

internal fun roomsStreamMessage(id: String, userId: String): String {
    return newSubscriptionMessage("stream-notify-user", id,
            "\"$userId/rooms-changed\", false")
}

internal const val CONNECT_MESSAGE = "{\"msg\":\"connect\",\"version\":\"1\",\"support\":[\"1\",\"pre2\",\"pre1\"]}"