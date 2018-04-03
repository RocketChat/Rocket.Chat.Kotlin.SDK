package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ReactionPayload(
    val messageId: String,
    val emoji: String
)