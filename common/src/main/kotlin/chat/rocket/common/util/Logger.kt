package chat.rocket.common.util

class Logger(val platformLogger: PlatformLogger, private val url: String) {

    val enabled: Boolean = true

    fun debug(msg: () -> Any?) {
        if (enabled) platformLogger.debug("SDK($url): ${msg.toStringSafe()}")
    }

    fun info(msg: () -> Any?) {
        if (enabled) platformLogger.info("SDK($url): ${msg.toStringSafe()}")
    }

    fun warn(msg: () -> Any?) {
        if (enabled) platformLogger.warn("SDK($url): ${msg.toStringSafe()}")
    }
}