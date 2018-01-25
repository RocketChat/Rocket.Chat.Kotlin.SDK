package chat.rocket.core.model.attachment

import chat.rocket.common.internal.ISO8601Date
import com.squareup.moshi.Json

data class MessageAttachment(
        @Json(name = "author_name") val author: String?,
        @Json(name = "author_icon") val icon: String?,
        val text: String?,
        val thumbUrl: String?,
        val color: String?,
        @Json(name = "message_link") private val messageLink: String?,
        val attachments: List<Attachment>?,
        @Json(name = "ts") @ISO8601Date val timestamp: Long?
) : Attachment {
    override val url: String
        get() = messageLink ?: ""
}