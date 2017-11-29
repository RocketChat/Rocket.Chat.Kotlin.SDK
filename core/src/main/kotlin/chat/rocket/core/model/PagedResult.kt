package chat.rocket.core.model

data class PagedResult<out T>(
        val result: T,
        val total: Long,
        val offset: Long
)