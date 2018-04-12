package chat.rocket.core.internal.model

import chat.rocket.core.model.attachment.Attachment
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class PostMessagePayload(
    val roomId: String,
    val text: String?,
    val alias: String?,
    val emoji: String?,
    val avatar: String?,
    val attachments: List<Attachment>?,
    val msgId: String? = null
)