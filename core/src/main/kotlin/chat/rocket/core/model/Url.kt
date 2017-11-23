package chat.rocket.core.model

data class Url(
        val url: String,
        val meta: Map<String, String>?,
        val headers: Map<String, String>?,
        val parsedUrl: ParsedUrl?,
        val ignoreParse: Boolean? = false
)