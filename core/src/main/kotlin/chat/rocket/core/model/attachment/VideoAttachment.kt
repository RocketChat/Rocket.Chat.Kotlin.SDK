package chat.rocket.core.model.attachment

import com.squareup.moshi.Json

data class VideoAttachment(
    override val title: String?,
    override val description: String?,
    override val text: String?,
    @Json(name = "title_link") override val titleLink: String?,
    @Json(name = "title_link_download") override val titleLinkDownload: Boolean?,
    @Json(name = "video_url") private val videoUrl: String,
    @Json(name = "video_type") private val videoType: String?,
    @Json(name = "video_size") private val videoSize: Long?
) : FileAttachment {
    override val url: String
        get() = videoUrl
    override val size: Long?
        get() = videoSize
    override val type: String?
        get() = videoType
}