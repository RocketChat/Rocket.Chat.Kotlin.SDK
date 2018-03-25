package chat.rocket.core.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class PagedResult<out T>(
    val result: T,
    val total: Long,
    val offset: Long
)