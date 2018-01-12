package chat.rocket.core.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Value<out T>(val value: T)