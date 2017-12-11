package chat.rocket.common.internal

data class AuthenticationErrorMessage(var message: String, var status: String, var error: String? = null)