package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
class UserUpdatePasswordPayload(val userId: String, val password: String)