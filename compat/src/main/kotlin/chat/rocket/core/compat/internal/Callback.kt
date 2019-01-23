package chat.rocket.core.compat.internal

import chat.rocket.common.RocketChatException
import chat.rocket.core.compat.Call
import chat.rocket.core.compat.Callback
import kotlinx.coroutines.AbstractCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.newCoroutineContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine

@JvmOverloads
fun <T> callback(
    context: CoroutineContext = Dispatchers.Default,
    callback: Callback<T>,
    block: suspend CoroutineScope.() -> T
): Call {
    val newContext = GlobalScope.newCoroutineContext(context)
    val job = Job(newContext[Job])
    val coroutine = CallbackCoroutine(newContext + job, callback)
    block.startCoroutine(coroutine, coroutine)
    return Call(job)
}

@UseExperimental(InternalCoroutinesApi::class)
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
