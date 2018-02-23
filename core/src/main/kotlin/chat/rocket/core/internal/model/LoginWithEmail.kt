package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class LoginWithEmail(
        val user: String,
        val password: String,
        val code: String? = null
)