package chat.rocket.core.model.attachment

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Field(val title: String, val value: String)
