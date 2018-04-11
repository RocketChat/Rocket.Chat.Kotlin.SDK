package chat.rocket.core.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Permission(
    @Json(name = "_id") val id: String,
    val roles: List<String>
)