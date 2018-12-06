package chat.rocket.core.model

import chat.rocket.common.internal.ISO8601Date
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class NewDirectMessageResult(
    @Json(name = "_id")
    val id: String,
    @Json(name = "_updatedAt")
    @ISO8601Date
    val updatedAt: Long? = null,
    @Json(name = "t")
    val type: MessageType? = null,
    @Json(name = "ts")
    @ISO8601Date
    val timestamp: Long,
    val usernames: List<String>
)
