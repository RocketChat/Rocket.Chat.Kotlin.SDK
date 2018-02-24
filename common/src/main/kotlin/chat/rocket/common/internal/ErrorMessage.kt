package chat.rocket.common.internal

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ErrorMessage(val error: String, val errorType: String?)