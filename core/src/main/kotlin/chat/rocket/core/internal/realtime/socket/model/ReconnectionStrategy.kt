package chat.rocket.core.internal.realtime.socket.model

open class ReconnectionStrategy(val maxAttempts: Int, private val interval: Int) {
    var numberOfAttempts: Int = 0

    fun processAttempts() {
        numberOfAttempts++
    }

    val reconnectInterval: Int
        get() {
            val value = interval * (numberOfAttempts + 1)
            return if (value > maxReconnectInterval) maxReconnectInterval else value
        }
}

private const val maxReconnectInterval = 30000