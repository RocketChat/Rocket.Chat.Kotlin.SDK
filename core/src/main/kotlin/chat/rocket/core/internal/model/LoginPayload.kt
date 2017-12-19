package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class LoginPayload(
        val username: String,
        val password: String,
        val code: String? = null
)