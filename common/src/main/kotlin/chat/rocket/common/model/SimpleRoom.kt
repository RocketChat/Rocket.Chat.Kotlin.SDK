package chat.rocket.common.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SimpleRoom(
        @Json(name = "_id") val id: String,
        val name: String?
)