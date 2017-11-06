package chat.rocket.common.model

interface BaseMessage {
    val roomId: String
    val message: String
    val timestamp: Long
    val sender: User?
    val updatedAt: Long
    val editedAt: Long?
    val editedBy: User?
//    // TODO - @Json(name = "t") @Nullable public abstract String type();
    val senderAlias: String?
}