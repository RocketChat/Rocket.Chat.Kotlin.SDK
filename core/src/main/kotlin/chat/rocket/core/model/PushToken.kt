package chat.rocket.core.model

import chat.rocket.common.internal.ISO8601Date
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class PushToken(
    @Json(name = "_id") val id: String,
    val appName: String,
    val userId: String,
    val enabled: Boolean,
    @Json(name = "createdAt") @ISO8601Date val createdAt: Long?,
    @Json(name = "updatedAt") @ISO8601Date val updatedAt: Long?
)