package chat.rocket.core.compat

import chat.rocket.core.RocketChatClient
import chat.rocket.core.compat.internal.callback
import chat.rocket.core.internal.rest.me
import chat.rocket.core.model.Myself
import kotlinx.coroutines.experimental.CommonPool

/**
 * Returns the current logged user information, useful to check if the Token from TokenProvider
 * is still valid. Must be used with a coroutine context (async, launch, etc)
 *
 * @return Call
 * @see
 * @see RocketChatException
 */
fun RocketChatClient.me(future: Callback<Myself>): Call =
        callback(CommonPool, future) {
            me()
        }