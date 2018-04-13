package chat.rocket.core.internal.realtime.message

/**
 * See https://rocket.chat/docs/developer-guides/realtime-api/#realtime-api
 */

// The server will send you “ping” and you must respond with “pong” otherwise the server will close the connection.
internal fun pongMessage(): String = "{\"msg\":\"pong\"}"

// The server will send you “ping” and you must respond with “pong” otherwise the server will close the connection.
internal fun pingMessage(): String = "{\"msg\":\"ping\"}"

// Before requesting any method/subscription you have to send a connect message:
internal const val CONNECT_MESSAGE = "{\"msg\":\"connect\",\"version\":\"1\",\"support\":[\"1\",\"pre2\",\"pre1\"]}"