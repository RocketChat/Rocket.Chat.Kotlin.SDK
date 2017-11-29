package chat.rocket.core.model

import chat.rocket.common.model.BaseRoom
import chat.rocket.common.model.SimpleUser
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.model.Subscription

data class ChatRoom(override val id: String,
                    override val type: BaseRoom.RoomType,
                    override val user: SimpleUser?,
                    override val name: String?,
                    override val fullName: String?,
                    override val readonly: Boolean? = false,
                    override val updatedAt: Long?,
                    val timestamp: Long?,
                    val lastModified: Long?,
                    val default: Boolean? = false,
                    val open: Boolean,
                    val alert: Boolean,
                    val unread: Long,
                    val userMenstions: Long,
                    val groupMentions: Long,
                    val client: RocketChatClient
) : BaseRoom {
    companion object {
        fun create(room: Room, subscription: Subscription, client: RocketChatClient): ChatRoom {
            return ChatRoom(id = room.id,
                    type = room.type,
                    user = room.user ?: subscription.user,
                    name = room.name ?: subscription.name,
                    fullName = room.fullName ?: subscription.fullName,
                    readonly = room.readonly ?: subscription.readonly,
                    updatedAt = room.updatedAt ?: subscription.updatedAt,
                    timestamp = subscription.timestamp,
                    lastModified = subscription.lastModified,
                    default = subscription.default,
                    open = subscription.open,
                    alert = subscription.alert,
                    unread = subscription.unread,
                    userMenstions = subscription.userMentions,
                    groupMentions = subscription.groupMentions,
                    client = client)
        }
    }
}