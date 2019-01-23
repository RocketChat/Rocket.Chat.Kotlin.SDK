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
    @Json(name = "_id")
    val id: String,
    @Json(name = "rid")
    override val roomId: String,
    @JsonDefaultValueString("")
    @Json(name = "msg")
    override val message: String = "",
    @Json(name = "ts")
    @ISO8601Date
    override val timestamp: Long,
    @Json(name = "u")
    override val sender: SimpleUser? = null,
    @Json(name = "_updatedAt")
    @ISO8601Date
    override val updatedAt: Long? = null,
    @ISO8601Date
    override val editedAt: Long? = null,
    override val editedBy: SimpleUser? = null,
    @Json(name = "alias")
    override val senderAlias: String? = null,
    override val avatar: String? = null,
    @Json(name = "t")
    val type: MessageType? = null,
    @JsonDefaultValueBoolean(false)
    val groupable: Boolean = false,
    @JsonDefaultValueBoolean(false)
    val parseUrls: Boolean = false,
    val urls: List<Url>? = null,
    override val mentions: List<SimpleUser>? = null,
    override val channels: List<SimpleRoom>? = null,
    val attachments: List<Attachment>? = null,
    @JsonDefaultValueBoolean(false)
    val pinned: Boolean = false,
    val starred: List<SimpleUser>? = null,
    val reactions: Reactions? = null,
    val role: String? = null,
    @JsonDefaultValueBoolean(true)
    override val synced: Boolean = true, // TODO: Remove after we have a db
    val unread: Boolean? = null
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

    @Json(name = "user-muted")
    class UserMuted : MessageType()

    @Json(name = "user-unmuted")
    class UserUnMuted : MessageType()

    @Json(name = "subscription-role-added")
    class SubscriptionRoleAdded : MessageType()

    @Json(name = "subscription-role-removed")
    class SubscriptionRoleRemoved : MessageType()

    @Json(name = "room_changed_privacy")
    class RoomChangedPrivacy : MessageType()

    @Json(name = "jitsi_call_started")
    class JitsiCallStarted : MessageType()

    class Unspecified(val rawType: String) : MessageType()
}

fun MessageType?.asString(): String? {
    return when (this) {
        is MessageType.RoomNameChanged -> "r"
        is MessageType.UserAdded -> "au"
        is MessageType.UserRemoved -> "ru"
        is MessageType.UserJoined -> "uj"
        is MessageType.UserLeft -> "ul"
        is MessageType.Welcome -> "wm"
        is MessageType.MessageRemoved -> "rm"
        is MessageType.MessagePinned -> "message_pinned"
        is MessageType.UserMuted -> "user-muted"
        is MessageType.UserUnMuted -> "user-unmuted"
        is MessageType.SubscriptionRoleAdded -> "subscription-role-added"
        is MessageType.SubscriptionRoleRemoved -> "subscription-role-removed"
        is MessageType.RoomChangedPrivacy -> "room_changed_privacy"
        is MessageType.JitsiCallStarted -> "jitsi_call_started"
        else -> null
    }
}

fun Message.isSystemMessage() = when (type) {
    is MessageType.MessageRemoved,
    is MessageType.UserJoined,
    is MessageType.UserLeft,
    is MessageType.UserAdded,
    is MessageType.RoomNameChanged,
    is MessageType.UserRemoved,
    is MessageType.UserMuted,
    is MessageType.UserUnMuted,
    is MessageType.SubscriptionRoleAdded,
    is MessageType.SubscriptionRoleRemoved,
    is MessageType.RoomChangedPrivacy,
    is MessageType.JitsiCallStarted,
    is MessageType.MessagePinned -> true
    else -> false
}

fun messageTypeOf(type: String?): MessageType? {
    return when (type) {
        "r" -> MessageType.RoomNameChanged()
        "au" -> MessageType.UserAdded()
        "ru" -> MessageType.UserRemoved()
        "uj" -> MessageType.UserJoined()
        "ul" -> MessageType.UserLeft()
        "wm" -> MessageType.Welcome()
        "rm" -> MessageType.MessageRemoved()
        "message_pinned" -> MessageType.MessagePinned()
        "user-muted" -> MessageType.UserMuted()
        "user-unmuted" -> MessageType.UserUnMuted()
        "subscription-role-added" -> MessageType.SubscriptionRoleAdded()
        "subscription-role-removed" -> MessageType.SubscriptionRoleAdded()
        "room_changed_privacy" -> MessageType.RoomChangedPrivacy()
        "jitsi_call_started" -> MessageType.JitsiCallStarted()
        null -> null
        else -> MessageType.Unspecified(type)
    }
}
