package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
class SignUpPayload(val username: String?,
                    val email: String?,
                    val password: String?,
                    val name: String?)
