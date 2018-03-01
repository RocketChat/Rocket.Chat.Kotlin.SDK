package chat.rocket.common.util

class Logger(val platformLogger: PlatformLogger) {

    val enabled: Boolean = true

    fun debug(msg: () -> Any?) {
        if (enabled) platformLogger.debug("SDK: ${msg.toStringSafe()}")
    }

    fun info(msg: () -> Any?) {
        if (enabled) platformLogger.info("SDK: ${msg.toStringSafe()}")
    }

    fun warn(msg: () -> Any?) {
        if (enabled) platformLogger.warn("SDK: ${msg.toStringSafe()}")
    }
}