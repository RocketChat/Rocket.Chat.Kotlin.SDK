package chat.rocket.core.model.attachment

import com.squareup.moshi.Json

data class ImageAttachment(
    override val title: String?,
    override val description: String?,
    override val text: String?,
    @Json(name = "title_link") override val titleLink: String?,
    @Json(name = "title_link_download") override val titleLinkDownload: Boolean?,
    @Json(name = "image_url") private val imageUrl: String,
    @Json(name = "image_type") private val imageType: String?,
    @Json(name = "image_size") private val imageSize: Long?,
    @Json(name = "image_preview") val imagePreview: String?
) : FileAttachment {
    override val url: String
        get() = imageUrl
    override val size: Long?
        get() = imageSize
    override val type: String?
        get() = imageType
}