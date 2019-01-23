package chat.rocket.core.compat

import chat.rocket.core.RocketChatClient
import chat.rocket.core.compat.internal.callback
import chat.rocket.core.internal.rest.me
import chat.rocket.core.model.Myself
import kotlinx.coroutines.Dispatchers

/**
 * Returns the current logged user information, useful to check if the Token from TokenProvider
 * is still valid. Must be used with a coroutine context (async, launch, etc)
 */
fun RocketChatClient.me(future: Callback<Myself>): Call = callback(Dispatchers.IO, future) { me() }