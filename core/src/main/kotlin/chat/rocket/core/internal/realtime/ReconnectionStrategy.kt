package chat.rocket.core.internal.realtime

open class ReconnectionStrategy(val maxAttempts: Int, reconnectInterval: Int) {
    var numberOfAttempts: Int = 0
    open var reconnectInterval: Int = 0
    private val maxReconnectInterval = 30000

    init {
        if (reconnectInterval < maxReconnectInterval) {
            this.reconnectInterval = reconnectInterval
        } else {
            this.reconnectInterval = maxReconnectInterval
        }
        numberOfAttempts = 0
    }

    fun processAttempts() {
        numberOfAttempts++
    }
}