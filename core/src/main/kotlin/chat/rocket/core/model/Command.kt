package chat.rocket.core.model

import se.ansman.kotshi.JsonDefaultValueBoolean
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Command(
    val command: String,
    val params: String?,
    val description: String? = null,
    @JsonDefaultValueBoolean(false) val clientOnly: Boolean = false
)