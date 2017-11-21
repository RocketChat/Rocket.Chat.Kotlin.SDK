package chat.rocket.core.internal.rest.coroutines

import chat.rocket.common.RocketChatException
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.me
import chat.rocket.core.model.Myself
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Returns the current logged user information, useful to check if the Token from TokenProvider
 * is still valid. Must be used with a coroutine context (async, launch, etc)
 *
 * @return current user Object
 * @see Myself
 * @see RocketChatException
 */
suspend fun RocketChatClient.me(): Myself =
        suspendCoroutine { continuation ->
            me(success = {
                continuation.resume(it)
            }, error = {
                continuation.resumeWithException(it)
            })
        }
