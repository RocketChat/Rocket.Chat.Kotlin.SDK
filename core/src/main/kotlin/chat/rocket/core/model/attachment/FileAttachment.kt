package chat.rocket.core.model.attachment

interface FileAttachment : Attachment {
    val title: String?
    val description: String?
    val text: String?
    val titleLink: String?
    val titleLinkDownload: Boolean?
    val type: String?
    val size: Long?
}