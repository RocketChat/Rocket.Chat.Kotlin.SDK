package chat.rocket.core.model

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.SimpleUser
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ReadReceipt(
    @Json(name = "_id") val id: String?,
    val roomId: String,
    val userId: String,
    val messageId: String,
    @Json(name = "ts")
    @ISO8601Date
    val timestamp: Long,
    val user: SimpleUser
)