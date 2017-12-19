package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class UserPayload(
        val email: String,
        val name: String,
        val pass: String,
        val username: String
)