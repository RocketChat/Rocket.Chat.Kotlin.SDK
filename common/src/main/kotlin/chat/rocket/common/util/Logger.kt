package chat.rocket.common.util

class Logger(var platformLogger: PlatformLogger) {

    val enabled: Boolean = true

    fun debug(msg: () -> Any?) {
        if (enabled) platformLogger.debug(msg.toStringSafe())
    }

    fun info(msg: () -> Any?) {
        if (enabled) platformLogger.info(msg.toStringSafe())
    }

    fun warn(msg: () -> Any?) {
        if (enabled) platformLogger.warn(msg.toStringSafe())
    }
}