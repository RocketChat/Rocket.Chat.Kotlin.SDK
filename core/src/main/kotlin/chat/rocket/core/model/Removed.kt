package chat.rocket.core.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Removed(
    @Json(name = "_id")
    val id: String
)