package chat.rocket.core.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Reaction(
    val shortname: String,
    val usernames: List<String>
)