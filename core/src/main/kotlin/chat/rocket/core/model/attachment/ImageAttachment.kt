package chat.rocket.core.model.attachment

import com.squareup.moshi.Json

data class ImageAttachment(
    override val title: String? = null,
    override val description: String? = null,
    override val text: String? = null,
    @Json(name = "title_link") override val titleLink: String? = null,
    @Json(name = "title_link_download") override val titleLinkDownload: Boolean? = null,
    @Json(name = "image_url") private val imageUrl: String,
    @Json(name = "image_type") private val imageType: String? = null,
    @Json(name = "image_size") private val imageSize: Long? = null,
    @Json(name = "image_preview") val imagePreview: String? = null
) : FileAttachment {
    override val url: String
        get() = imageUrl
    override val size: Long?
        get() = imageSize
    override val type: String?
        get() = imageType
}