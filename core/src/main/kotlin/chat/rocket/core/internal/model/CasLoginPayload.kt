package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class CasLoginPayload(val cas: CasData)

@JsonSerializable
data class CasData(val credentialToken: String)