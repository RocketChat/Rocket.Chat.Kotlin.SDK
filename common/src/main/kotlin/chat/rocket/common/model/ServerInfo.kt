package chat.rocket.common.model

data class ServerInfo(
    val version: String,
    val url: String,
    val redirected: Boolean = false
)