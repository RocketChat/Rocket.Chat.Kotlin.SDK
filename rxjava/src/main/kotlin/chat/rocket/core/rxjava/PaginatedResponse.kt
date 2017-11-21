package chat.rocket.core.rxjava

data class PaginatedResponse<out T>(
    val data: List<T>,
    val total: Long
)