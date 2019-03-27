package chat.rocket.common.util

interface Logger {
    fun debug(msg: () -> Any?)
    fun info(msg: () -> Any?)
    fun warn(msg: () -> Any?)
}

class RealLogger(val platformLogger: PlatformLogger, private val url: String) : Logger {


    override fun debug(msg: () -> Any?) {
        platformLogger.debug("SDK($url): ${msg.toStringSafe()}")
    }

    override fun info(msg: () -> Any?) {
        platformLogger.info("SDK($url): ${msg.toStringSafe()}")
    }

    override fun warn(msg: () -> Any?) {
        platformLogger.warn("SDK($url): ${msg.toStringSafe()}")
    }
}

object NoOpLogger : Logger {
    override fun debug(msg: () -> Any?) {
    }

    override fun info(msg: () -> Any?) {
    }

    override fun warn(msg: () -> Any?) {
    }
}