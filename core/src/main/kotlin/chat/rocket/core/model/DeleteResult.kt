package chat.rocket.core.model

import chat.rocket.common.internal.ISO8601Date
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonDefaultValueBoolean
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class DeleteResult(
        @Json(name = "_id") val id: String,
        @Json(name = "ts") @ISO8601Date val timestamp: Long,
        @JsonDefaultValueBoolean(false)
        val success: Boolean
)