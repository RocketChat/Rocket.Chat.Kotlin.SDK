package chat.rocket.core.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class UserRole(
    @Json(name = "_id") val id: String,
    val username: String,
    val roles: List<String>
)