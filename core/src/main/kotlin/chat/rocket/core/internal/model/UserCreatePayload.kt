package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class UserCreatePayload(val email: String,
                             val name: String,
                             val password: String,
                             val username: String)