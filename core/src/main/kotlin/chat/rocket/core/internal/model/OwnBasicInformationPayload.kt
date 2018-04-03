package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class OwnBasicInformationPayload(val data: OwnBasicInformationPayloadData)

@JsonSerializable
data class OwnBasicInformationPayloadData(
    val email: String?,
    val currentPassword: String?,
    val newPassword: String?,
    val username: String?,
    val name: String?
)