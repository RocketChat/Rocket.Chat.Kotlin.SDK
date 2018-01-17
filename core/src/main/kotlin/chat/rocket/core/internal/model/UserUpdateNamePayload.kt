package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
class UserUpdateNamePayload(val userId: String, val name: String)