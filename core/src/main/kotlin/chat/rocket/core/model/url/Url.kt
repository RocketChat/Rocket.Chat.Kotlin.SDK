package chat.rocket.core.model.url

import se.ansman.kotshi.JsonDefaultValueBoolean
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Url(
    val url: String,
    val meta: Meta?,
    val headers: Map<String, String>?,
    val parsedUrl: ParsedUrl?,
    @JsonDefaultValueBoolean(false)
    val ignoreParse: Boolean
)