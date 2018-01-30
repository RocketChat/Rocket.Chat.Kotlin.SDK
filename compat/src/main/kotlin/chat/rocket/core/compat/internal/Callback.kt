package chat.rocket.core.compat.internal

import chat.rocket.common.RocketChatException
import chat.rocket.core.compat.Call
import chat.rocket.core.compat.Callback
import kotlinx.coroutines.experimental.AbstractCoroutine
import kotlinx.coroutines.experimental.CompletedExceptionally
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.newCoroutineContext
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.startCoroutine

@JvmOverloads
fun <T> callback(context: CoroutineContext = DefaultDispatcher,
                 callback: Callback<T>,
                 block: suspend CoroutineScope.() -> T
): Call {
    val newContext = newCoroutineContext(context)
    val job = Job(newContext[Job])
    val coroutine = CallbackCoroutine(newContext + job, callback)
    block.startCoroutine(coroutine, coroutine)
    return Call(job)
}

private class CallbackCoroutine<in T>(
        parentContext: CoroutineContext,
        private val callback: Callback<T>
) : AbstractCoroutine<T>(parentContext, true) {
    override fun onCompleted(value: T) {
        callback.onSuccess(value)
    }

    override fun onCompletedExceptionally(exception: Throwable) {
        if (exception is RocketChatException) {
            callback.onError(exception)
        } else {
            callback.onError(RocketChatException(exception.message ?: "Unknown Error", exception))
        }
    }
}
