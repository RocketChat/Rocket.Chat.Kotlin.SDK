package chat.rocket.core.model

import chat.rocket.common.internal.ISO8601Date
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class DirectoryResult(
    @Json(name = "_id") val id: String,
    val name: String,
    val username: String?,
    @ISO8601Date val createdAt: Long?,
    @Json(name = "ts") @ISO8601Date val timestamp: Long?
)
