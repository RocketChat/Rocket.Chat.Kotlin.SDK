package chat.rocket.core.model.attachment

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

data class AudioAttachment(override val title: String,
                           override val description: String,
                           override val author: String?,
                           override val text: String?,
                           @Json(name = "thumb_url") override val thumbUrl: String?,
                           override val color: String?,
                           @Json(name = "title_link") override val titleLink: String,
                           @Json(name = "title_link_download") override val titleLinkDownload: Boolean,
                           @Json(name = "audio_url") private val audioUrl: String,
                           @Json(name = "audio_type") private val audioType: String,
                           @Json(name = "audio_url") private val audioSize: Long
) : Attachment {
    override val url: String
        get() = audioUrl
    override val size: Long
        get() = audioSize
    override val type: String
        get() = audioType
}