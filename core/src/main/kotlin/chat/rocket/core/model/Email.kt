package chat.rocket.core.model

import se.ansman.kotshi.JsonDefaultValueBoolean
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Email(
    val address: String?,
    @JsonDefaultValueBoolean(false)
    val verified: Boolean
)