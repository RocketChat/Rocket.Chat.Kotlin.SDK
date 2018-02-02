package chat.rocket.core.model.url

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ParsedUrl(
        val host: String?,
        val hash: String?,
        val pathname: String?,
        val protocol: String?,
        val port: String?,
        // TODO - bring back the query value - it can be a MAP or a Single String. on the sdk will always be a Map
        // with a custom adapter
        val search: String?,
        val hostname: String?
)