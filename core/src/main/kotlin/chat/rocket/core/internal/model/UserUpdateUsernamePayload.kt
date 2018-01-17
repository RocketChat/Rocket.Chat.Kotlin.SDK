package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
class UserUpdateUsernamePayload(val userId: String, val username: String)