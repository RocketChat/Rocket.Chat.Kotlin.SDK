package chat.rocket.common.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Token(val userId: String, val authToken: String)