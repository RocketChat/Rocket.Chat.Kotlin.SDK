package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class UserPayload(
    val userId: String?,
    val data: UserPayloadData?,
    val avatarUrl: String?
)

@JsonSerializable
data class UserPayloadData(
    val name: String?,
    val password: String?,
    val username: String?,
    val email: String?
)