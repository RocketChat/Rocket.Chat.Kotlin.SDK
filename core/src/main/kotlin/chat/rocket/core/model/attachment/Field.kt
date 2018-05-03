package chat.rocket.core.model.attachment

import se.ansman.kotshi.JsonDefaultValueString
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Field(
    @JsonDefaultValueString("")
    val title: String,
    @JsonDefaultValueString("")
    val value: String
)