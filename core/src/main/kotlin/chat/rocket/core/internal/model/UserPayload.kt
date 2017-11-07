package chat.rocket.core.internal.model

data class UserPayload(
        val email: String,
        val name: String,
        val pass: String,
        val username: String
)