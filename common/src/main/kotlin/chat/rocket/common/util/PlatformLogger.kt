package chat.rocket.common.util

interface PlatformLogger {
    fun debug(s: String)

    fun info(s: String)

    fun warn(s: String)
}