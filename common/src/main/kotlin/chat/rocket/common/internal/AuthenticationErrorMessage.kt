package chat.rocket.common.internal

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class AuthenticationErrorMessage(var message: String, var status: String, var error: String? = null)