package chat.rocket.core.model.attachment

data class GenericFileAttachment(
    override val title: String? = null,
    override val description: String? = null,
    override val text: String? = null,
    override val titleLink: String? = null,
    val fileUrl: String,
    override val titleLinkDownload: Boolean? = null
) : FileAttachment {
    override val url: String
        get() = fileUrl
    override val size: Long?
        get() = null
    override val type: String?
        get() = null
}