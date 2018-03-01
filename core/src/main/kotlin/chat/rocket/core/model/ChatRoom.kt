package chat.rocket.core.model

import chat.rocket.common.model.BaseRoom
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.SimpleUser
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.model.Subscription
import chat.rocket.core.internal.realtime.subscribeRoomMessages
import chat.rocket.core.internal.rest.history
import chat.rocket.core.internal.rest.messages
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext

data class ChatRoom(override val id: String,
                    override val type: RoomType,
                    override val user: SimpleUser?,
                    val name: String,
                    override val fullName: String?,
                    override val readonly: Boolean? = false,
                    override val updatedAt: Long?,
                    val timestamp: Long?,
                    val lastSeen: Long?,
                    val topic: String?,
                    val announcement: String?,
                    @get:JvmName("isDefault")
                    val default: Boolean = false,
                    val open: Boolean,
                    val alert: Boolean,
                    val unread: Long,
                    val userMenstions: Long?,
                    val groupMentions: Long?,
                    val lastMessage: Message?,
                    val client: RocketChatClient
) : BaseRoom {
    companion object {
        fun create(room: Room, subscription: Subscription, client: RocketChatClient): ChatRoom {
            return ChatRoom(id = room.id,
                            type = room.type,
                            user = room.user ?: subscription.user,
                            name = room.name ?: subscription.name,
                            fullName = room.fullName ?: subscription.fullName,
                            readonly = room.readonly,
                            updatedAt = room.updatedAt ?: subscription.updatedAt,
                            timestamp = subscription.timestamp,
                            lastSeen = subscription.lastSeen,
                            topic = room.topic,
                            announcement = room.announcement,
                            default = subscription.isDefault,
                            open = subscription.open,
                            alert = subscription.alert,
                            unread = subscription.unread,
                            userMenstions = subscription.userMentions,
                            groupMentions = subscription.groupMentions,
                            lastMessage = room.lastMessage,
                            client = client)
        }
    }

    val lastModified: Long?
        get() = lastSeen
}

suspend fun ChatRoom.messages(offset: Long = 0,
                              count: Long = 50): PagedResult<List<Message>> = withContext(CommonPool) {
    return@withContext client.messages(id, type, offset, count)
}

suspend fun ChatRoom.history(count: Long = 50,
                             oldest: String? = null,
                             latest: String? = null): PagedResult<List<Message>> = withContext(CommonPool) {
    return@withContext client.history(id, type, count, oldest, latest)
}

fun ChatRoom.subscribeMessages(callback: (Boolean, String) -> Unit): String {
    return client.subscribeRoomMessages(id, callback)
}
