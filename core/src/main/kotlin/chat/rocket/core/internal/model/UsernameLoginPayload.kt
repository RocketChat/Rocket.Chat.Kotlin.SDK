package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class UsernameLoginPayload(
    val username: String,
    val password: String,
    val code: String? = null
)