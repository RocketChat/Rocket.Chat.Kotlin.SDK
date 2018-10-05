package chat.rocket.common.model

interface BaseMessage {
    val roomId: String
    val message: String
    val timestamp: Long
    val sender: SimpleUser?
    val updatedAt: Long?
    val editedAt: Long?
    val editedBy: SimpleUser?
    val senderAlias: String?
    val avatar: String?
    val mentions: List<SimpleUser>?
    val channels: List<SimpleRoom>?
    val synced: Boolean? //TODO: Remove after we have a db
}