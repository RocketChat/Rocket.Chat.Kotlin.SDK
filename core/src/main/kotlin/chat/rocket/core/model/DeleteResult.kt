package chat.rocket.core.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class DeleteResult(
    @Json(name = "_id") val id: String,
    @Json(name = "ts") val timestamp: Long?,
    val success: Boolean
)
