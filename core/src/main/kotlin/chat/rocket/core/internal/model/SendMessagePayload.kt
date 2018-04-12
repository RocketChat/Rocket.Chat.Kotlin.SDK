package chat.rocket.core.internal.model

import chat.rocket.core.model.attachment.Attachment
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
internal data class SendMessageBody(
    @Json(name = "_id") val id: String? = null,
    val rid: String,
    val msg: String?,
    val alias: String?,
    val emoji: String?,
    val avatar: String?,
    val attachments: List<Attachment>?,
    val msgId: String? = null
)

internal data class SendMessagePayload(
    val message: SendMessageBody
)