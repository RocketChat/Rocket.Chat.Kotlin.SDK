package chat.rocket.core.model.attachment

import com.squareup.moshi.Json

data class VideoAttachment(
    override val title: String? = null,
    override val description: String? = null,
    override val text: String? = null,
    @Json(name = "title_link") override val titleLink: String? = null,
    @Json(name = "title_link_download") override val titleLinkDownload: Boolean? = null,
    @Json(name = "video_url") private val videoUrl: String,
    @Json(name = "video_type") private val videoType: String? = null,
    @Json(name = "video_size") private val videoSize: Long? = null
) : FileAttachment {
    override val url: String
        get() = videoUrl
    override val size: Long?
        get() = videoSize
    override val type: String?
        get() = videoType
}