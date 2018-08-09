package chat.rocket.core.internal.realtime.socket.model

open class ReconnectionStrategy(
    val maxAttempts: Int = INFINITE,
    private val interval: Int = DEFAULT_RECONNECT_INTERVAL
) {

    var numberOfAttempts: Int = 0

    fun processAttempts() {
        numberOfAttempts++
    }

    val reconnectInterval: Int
        get() {
            val value = interval * (numberOfAttempts + 1)
            return if (value > MAX_RECONNECT_INTERVAL) MAX_RECONNECT_INTERVAL else value
        }

    val shouldRetry: Boolean
        get() = maxAttempts == INFINITE || numberOfAttempts < maxAttempts

    fun reset() {
        numberOfAttempts = 0
    }

    companion object {
        const val INFINITE: Int = -1
        const val DEFAULT_RECONNECT_INTERVAL = 3000
        private const val MAX_RECONNECT_INTERVAL = 30000
    }
}