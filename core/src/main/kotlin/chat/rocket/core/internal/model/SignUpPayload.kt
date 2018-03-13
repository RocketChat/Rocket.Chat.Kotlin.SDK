package chat.rocket.core.internal.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
class SignUpPayload(
        val username: String?,
        val email: String?,
        @Json(name = "pass") val password: String?,
        val name: String?
)