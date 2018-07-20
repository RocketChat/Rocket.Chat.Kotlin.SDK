package chat.rocket.core.model

import chat.rocket.common.internal.ISO8601Date
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class CustomEmoji(
    @Json(name = "_id") val id: String,
    val name: String,
    val aliases: List<String> = emptyList(),
    val extension: String,
    @Json(name = "_updatedAt") @ISO8601Date val updatedAt: Long
)