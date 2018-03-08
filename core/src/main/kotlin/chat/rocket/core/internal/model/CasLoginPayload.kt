package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class CasLoginPayload(val cas: Data)

@JsonSerializable
data class Data(val credentialToken: String)