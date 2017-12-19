package chat.rocket.common.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ServerInfo(val version: String)