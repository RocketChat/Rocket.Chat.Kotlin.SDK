package chat.rocket.core.model

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.BaseMessage
import chat.rocket.common.model.SimpleUser
import chat.rocket.core.model.attachment.Attachment
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class LastMessage(
    @Json(name = "_id") override val id: String?, // The id of the last message
    @Json(name = "rid") override val roomId: String?, // The room id of the last message
    @Json(name = "msg") override val message: String? = "", // The content of the last message
    @Json(name = "ts") @ISO8601Date override val timestamp: Long?, // The timestamp of the last message
    @Json(name = "u") override val sender: SimpleUser? = null, // The sender of the last message
    val attachments: List<Attachment>? = null, // Case message content is empty and attachments is not empty then it means that an attachment was sent.
    val unread: Boolean? = null // if the last message was not read by the logged user yet
) : BaseMessage