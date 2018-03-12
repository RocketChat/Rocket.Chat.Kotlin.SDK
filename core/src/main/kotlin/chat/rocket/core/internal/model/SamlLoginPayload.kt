package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SamlLoginPayload(val saml: Boolean = true, val credentialToken: String)