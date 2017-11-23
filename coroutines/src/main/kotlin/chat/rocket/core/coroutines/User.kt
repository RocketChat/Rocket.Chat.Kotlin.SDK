package chat.rocket.core.coroutines

import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.channelSubscriptions
import chat.rocket.core.internal.rest.dmSubscriptions
import chat.rocket.core.internal.rest.groupSubscriptions
import chat.rocket.core.internal.rest.me
import chat.rocket.core.model.Myself
import chat.rocket.core.model.Room
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.Deferred
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Returns the current logged user information, useful to check if the Token from TokenProvider
 * is still valid. Must be used with a coroutine context (async, launch, etc)
 *
 * @return current user Object
 * @see Myself
 * @see RocketChatException
 */
//suspend fun RocketChatClient.me(): Myself =
//        suspendCoroutine { continuation ->
//            me(success = {
//                continuation.resume(it)
//            }, error = {
//                continuation.resumeWithException(it)
//            })
//        }

fun RocketChatClient.me(): Deferred<Myself> {
    val deferred = CompletableDeferred<Myself>()

    me(success = {
        deferred.complete(it)
    }, error = {
        deferred.completeExceptionally(it)
    })

    return deferred
}

fun RocketChatClient.channelSubscriptions(offset: Long? = 0): Deferred<Pair<List<Room>, Long>> {
    val deferred = CompletableDeferred<Pair<List<Room>, Long>>()

    channelSubscriptions(offset, success = {subscriptions, total ->
        deferred.complete(Pair(subscriptions, total))
    }, error = {
        deferred.completeExceptionally(it)
    })

    return deferred
}

fun RocketChatClient.groupSubscriptions(offset: Long? = 0): Deferred<Pair<List<Room>, Long>> {
    val deferred = CompletableDeferred<Pair<List<Room>, Long>>()

    groupSubscriptions(offset, success = {subscriptions, total ->
        deferred.complete(Pair(subscriptions, total))
    }, error = {
        deferred.completeExceptionally(it)
    })

    return deferred
}

fun RocketChatClient.dmSubscriptions(offset: Long? = 0): Deferred<Pair<List<Room>, Long>> {
    val deferred = CompletableDeferred<Pair<List<Room>, Long>>()

    dmSubscriptions(offset, success = {subscriptions, total ->
        deferred.complete(Pair(subscriptions, total))
    }, error = {
        deferred.completeExceptionally(it)
    })

    return deferred
}