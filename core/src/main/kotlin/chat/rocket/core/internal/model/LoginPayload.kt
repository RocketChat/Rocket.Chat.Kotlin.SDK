package chat.rocket.core.internal.model

data class LoginPayload(
        val username: String,
        val password: String,
        val code: String? = null
)