package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class PushRegistrationPayload(
    val type: String = "gcm",
    val value: String,
    val appName: String = "Main"
)

@JsonSerializable
data class PushUnregistrationPayload(val token: String)