package chat.rocket.core.model.attachment

interface Attachment {
    val title: String
    val description: String
    val author: String?
    val text: String?
    val thumbUrl: String?
    val color: String?
    val titleLink: String?
    val titleLinkDownload: Boolean
    val url: String
    val type: String
    val size: Long
}