package chat.rocket.core.model

import chat.rocket.common.internal.FallbackSealedClass
import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.BaseMessage
import chat.rocket.common.model.SimpleRoom
import chat.rocket.common.model.SimpleUser
import chat.rocket.core.model.attachment.Attachment
import chat.rocket.core.model.url.Url
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonDefaultValueBoolean
import se.ansman.kotshi.JsonDefaultValueString
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Message(
        @Json(name = "_id") val id: String,
        @Json(name = "rid") override val roomId: String,
        @JsonDefaultValueString("")
        @Json(name = "msg")
        override val message: String,
        @Json(name = "ts") @ISO8601Date override val timestamp: Long,
        @Json(name = "u") override val sender: SimpleUser?,
        @Json(name = "_updatedAt") @ISO8601Date override val updatedAt: Long?,
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
        val attachments: List<Attachment>?,
        @JsonDefaultValueBoolean(false)
        val pinned: Boolean,
        val reactions: Reactions?
) : BaseMessage

@FallbackSealedClass(name = "Unspecified", fieldName = "rawType")
sealed class MessageType {
    @Json(name = "r")
    class RoomNameChanged : MessageType()

    @Json(name = "au")
    class UserAdded : MessageType()

    @Json(name = "ru")
    class UserRemoved : MessageType()

    @Json(name = "uj")
    class UserJoined : MessageType()

    @Json(name = "ul")
    class UserLeft : MessageType()

    @Json(name = "wm")
    class Welcome : MessageType()

    @Json(name = "rm")
    class MessageRemoved : MessageType()

    @Json(name = "message_pinned")
    class MessagePinned : MessageType()

    class Unspecified(val rawType: String) : MessageType()
}

fun Message.isSystemMessage() = when (type) {
    is MessageType.MessageRemoved,
    is MessageType.UserJoined,
    is MessageType.UserLeft,
    is MessageType.UserAdded,
    is MessageType.RoomNameChanged,
    is MessageType.UserRemoved,
    is MessageType.MessagePinned -> true
    else -> false
}