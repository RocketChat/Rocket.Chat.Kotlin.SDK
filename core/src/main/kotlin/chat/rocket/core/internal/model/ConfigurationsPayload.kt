package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ConfigurationsPayload(val configurations: List<Map<String, String>>)