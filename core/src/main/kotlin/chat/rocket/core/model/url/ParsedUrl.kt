package chat.rocket.core.model.url

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ParsedUrl(
    val host: String? = null,
    val hash: String? = null,
    val pathname: String? = null,
    val protocol: String? = null,
    val port: String? = null,
        // TODO - bring back the query value - it can be a MAP or a Single String. on the sdk will always be a Map
        // with a custom adapter
    val search: String? = null,
    val hostname: String? = null
)