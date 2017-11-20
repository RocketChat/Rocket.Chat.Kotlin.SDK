package chat.rocket.common.util

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

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

fun <T> asyncTask(function: () -> T): Deferred<T> {
    return async(CommonPool) { function() }
}