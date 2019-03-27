package chat.rocket.core.model

import chat.rocket.common.model.BaseRoom
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.SimpleUser
import chat.rocket.common.model.UserStatus
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.model.Subscription
import chat.rocket.core.internal.realtime.subscribeRoomMessages
import chat.rocket.core.internal.rest.history
import chat.rocket.core.internal.rest.messages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ChatRoom(
    override val id: String,
    val subscriptionId: String,
    override val type: RoomType,
    override val user: SimpleUser?,
    val status: UserStatus?,
    val name: String,
    override val fullName: String?,
    override val readonly: Boolean? = false,
    override val updatedAt: Long?,
    val timestamp: Long?,
    val lastSeen: Long?,
    val topic: String?,
    val description: String?,
    val announcement: String?,
    @get:JvmName("isDefault")
    val default: Boolean = false,
    val favorite: Boolean = false,
    val open: Boolean,
    val alert: Boolean,
    val unread: Long,
    val roles: List<String>?,
    val archived: Boolean,
    val userMentions: Long?,
    val groupMentions: Long?,
    val lastMessage: Message?,
    val client: RocketChatClient,
    val broadcast: Boolean,
    @JvmField val muted: List<String>? = null
) : BaseRoom {
    companion object {
        fun create(room: Room, subscription: Subscription, client: RocketChatClient): ChatRoom {
            return ChatRoom(id = room.id,
                subscriptionId = subscription.id,
                type = room.type,
                user = room.user ?: subscription.user,
                status = null,
                name = room.name ?: subscription.name!!, // we guarantee on listSubscriptions() that it has a name
                fullName = room.fullName ?: subscription.fullName,
                readonly = room.readonly,
                updatedAt = room.updatedAt ?: subscription.updatedAt,
                timestamp = subscription.timestamp,
                lastSeen = subscription.lastSeen,
                topic = room.topic,
                description = room.description,
                announcement = room.announcement,
                default = subscription.isDefault,
                favorite = subscription.isFavorite,
                open = subscription.open,
                alert = subscription.alert,
                unread = subscription.unread,
                roles = subscription.roles,
                archived = subscription.archived,
                userMentions = subscription.userMentions,
                groupMentions = subscription.groupMentions,
                lastMessage = room.lastMessage,
                client = client,
                broadcast = room.broadcast,
                muted = room.muted
            )
        }
    }

    val lastModified: Long?
        get() = lastSeen
}

suspend fun ChatRoom.messages(
    offset: Long = 0,
    count: Long = 50
): PagedResult<List<Message>> = withContext(Dispatchers.IO) {
    return@withContext client.messages(id, type, offset, count)
}

suspend fun ChatRoom.history(
    count: Long = 50,
    oldest: String? = null,
    latest: String? = null
): PagedResult<List<Message>> = withContext(Dispatchers.IO) {
    return@withContext client.history(id, type, count, oldest, latest)
}

fun ChatRoom.subscribeMessages(callback: (Boolean, String) -> Unit): String {
    return client.subscribeRoomMessages(id, callback)
}

fun ChatRoom.userId(): String? {
    if (type !is RoomType.DirectMessage) return null

    return user?.id?.let { userId ->
        id.replace(userId, "")
    }
}