package chat.rocket.core.model.attachment

import com.squareup.moshi.Json

data class AudioAttachment(
    override val title: String?,
    override val description: String?,
    override val text: String?,
    @Json(name = "title_link") override val titleLink: String?,
    @Json(name = "title_link_download") override val titleLinkDownload: Boolean?,
    @Json(name = "audio_url") private val audioUrl: String,
    @Json(name = "audio_type") private val audioType: String?,
    @Json(name = "audio_size") private val audioSize: Long?
) : FileAttachment {
    override val url: String
        get() = audioUrl
    override val size: Long?
        get() = audioSize
    override val type: String?
        get() = audioType
}