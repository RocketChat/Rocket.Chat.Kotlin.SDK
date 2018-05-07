package chat.rocket.common.model

import okhttp3.HttpUrl

data class ServerInfo(
    val version: String,
    val url: HttpUrl,
    val redirected: Boolean = false
)