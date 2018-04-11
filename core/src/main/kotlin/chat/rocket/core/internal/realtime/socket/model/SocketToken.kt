package chat.rocket.core.internal.realtime.socket.model

import chat.rocket.common.internal.ISO8601Date
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SocketToken(
    @Json(name = "id")
    val userId: String,
    @Json(name = "token")
    val authToken: String,
    @Json(name = "tokenExpires") @ISO8601Date
    val expiresAt: Long?
)