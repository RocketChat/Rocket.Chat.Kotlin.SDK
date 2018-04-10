package chat.rocket.core.model.attachment

import com.squareup.moshi.Json

data class AuthorAttachment(
    @Json(name = "author_link")
    private val authorLink: String,
    @Json(name = "author_icon")
    val authorIcon: String?,
    @Json(name = "author_name")
    val authorName: String?,
    val fields: List<Field>?
) : Attachment {
    override val url: String
        get() = authorLink
}