package chat.rocket.core.model.attachment.actions

import chat.rocket.core.model.attachment.Attachment

data class ActionsAttachment(
    val title: String?,
    val actions: List<Action>,
    val buttonAlignment : String = "vertical"
) : Attachment {
    override val url: String
        get() = ""
}
