package chat.rocket.core.model.attachment

data class GenericFileAttachment(
    override val title: String?,
    override val description: String?,
    override val text: String?,
    override val titleLink: String?,
    val fileUrl: String,
    override val titleLinkDownload: Boolean?
) : FileAttachment {
    override val url: String
        get() = fileUrl
    override val size: Long?
        get() = null
    override val type: String?
        get() = null
}