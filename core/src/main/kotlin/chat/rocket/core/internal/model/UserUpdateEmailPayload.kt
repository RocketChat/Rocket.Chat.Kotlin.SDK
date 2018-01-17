package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
class UserUpdateEmailPayload(val userId: String, val email: String)