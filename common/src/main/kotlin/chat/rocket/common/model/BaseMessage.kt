package chat.rocket.common.model

interface BaseMessage {
    val id: String?
    val roomId: String?
    val message: String?
    val timestamp: Long?
    val sender: SimpleUser?
}