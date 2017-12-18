package chat.rocket.common.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Email(val address: String, val verified: Boolean)