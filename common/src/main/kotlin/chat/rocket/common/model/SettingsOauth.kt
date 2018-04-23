package chat.rocket.common.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SettingsOauth(val services: List<Map<String, Any>>)