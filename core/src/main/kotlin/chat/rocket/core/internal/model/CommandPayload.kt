package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class CommandPayload(
    val command: String,
    val roomId: String,
    val params: String?
)