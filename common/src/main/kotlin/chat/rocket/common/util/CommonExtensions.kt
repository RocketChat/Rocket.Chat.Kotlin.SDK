package chat.rocket.common.util

fun Any?.ifNull(block: () -> Unit) {
    if (this == null) block()
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun (() -> Any?).toStringSafe(): String {
    try {
        return invoke().toString()
    } catch (e: Exception) {
        return "Log message invocation failed: $e"
    }
}