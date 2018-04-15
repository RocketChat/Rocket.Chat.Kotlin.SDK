package chat.rocket.core.internal.realtime.socket.message.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SocketMessage(
    @Json(name = "msg")
    val type: MessageType,
    val id: String?,
    val collection: String?,
    @Json(name = "reason")
    val errorReason: String?
)

enum class MessageType {
    @Json(name = "connected")
    CONNECTED,
    @Json(name = "result")
    RESULT,
    @Json(name = "ready")
    READY,
    @Json(name = "nosub")
    UNSUBSCRIBED,
    @Json(name = "updated")
    UPDATED,
    @Json(name = "added")
    ADDED,
    @Json(name = "changed")
    CHANGED,
    @Json(name = "removed")
    REMOVED,
    @Json(name = "ping")
    PING,
    @Json(name = "pong")
    PONG,
    @Json(name = "error")
    ERROR
}
