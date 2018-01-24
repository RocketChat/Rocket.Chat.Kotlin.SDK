package chat.rocket.core.model

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.BaseMessage
import chat.rocket.common.model.SimpleRoom
import chat.rocket.common.model.SimpleUser
import chat.rocket.core.internal.FallbackEnum
import chat.rocket.core.model.attachment.Attachment
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonDefaultValueBoolean
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Message(
        @Json(name = "_id") val id: String,
        @Json(name = "rid") override val roomId: String,
        @Json(name = "msg") override val message: String,
        @Json(name = "ts") @ISO8601Date override val timestamp: Long,
        @Json(name = "u") override val sender: SimpleUser?,
        @Json(name = "_updatedAt") @ISO8601Date override val updatedAt: Long,
        @ISO8601Date override val editedAt: Long?,
        override val editedBy: SimpleUser?,
        @Json(name = "alias") override val senderAlias: String?,
        override val avatar: String?,
        @Json(name = "t") val type: MessageType?,
        @JsonDefaultValueBoolean(false)
        val groupable: Boolean,
        @JsonDefaultValueBoolean(false)
        val parseUrls: Boolean,
        val urls: List<Url>?,
        override val mentions: List<SimpleUser>?,
        override val channels: List<SimpleRoom>?,
        val attachments: List<Attachment>?
) : BaseMessage

enum @FallbackEnum(name = "UNSPECIFIED") class MessageType {
        @Json(name = "r")
        ROOM_NAME_CHANGED,
        @Json(name = "au")
        USER_ADDED,
        @Json(name = "ru")
        USER_REMOVED,
        @Json(name = "uj")
        USER_JOINED,
        @Json(name = "ul")
        USER_LEFT,
        @Json(name = "wm")
        WELCOME,
        @Json(name = "rm")
        MESSAGE_REMOVED,
        @Json(name = "")
        UNSPECIFIED
}