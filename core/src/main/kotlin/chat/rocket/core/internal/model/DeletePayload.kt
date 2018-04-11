package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class DeletePayload(
    val roomId: String,
    val msgId: String,
    val asUser: Boolean = false
)