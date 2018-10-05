package chat.rocket.core.model.url

import se.ansman.kotshi.JsonDefaultValueBoolean
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Url(
    val url: String,
    val meta: Meta? = null,
    val headers: Map<String, String>? = null,
    val parsedUrl: ParsedUrl? = null,
    @JsonDefaultValueBoolean(false)
    val ignoreParse: Boolean = false
)