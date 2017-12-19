package chat.rocket.core.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ParsedUrl(
        val host: String?,
        val hash: String?,
        val pathname: String?,
        val protocol: String?,
        val port: String?,
        val query: String?,
        val search: String?,
        val hostname: String?
)