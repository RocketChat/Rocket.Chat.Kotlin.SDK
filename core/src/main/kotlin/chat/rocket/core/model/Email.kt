package chat.rocket.core.model

data class Email(
        val address: String?,
        val verified: Boolean? = false
)