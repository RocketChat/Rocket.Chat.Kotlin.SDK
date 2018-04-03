package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class OauthLoginPayload(val oauth: OauthData)

@JsonSerializable
data class OauthData(val credentialToken: String, val credentialSecret: String)