package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.common.model.User
import chat.rocket.core.RocketChatClient

/**
 * Quick information about the authenticated user.
 */
fun RocketChatClient.me(success: (User) -> Unit,
                        error: (RocketChatException) -> Unit) {
    val url = requestUrl(restUrl, "me").build()

    val request = requestBuilder(url).get().build()

    handleRestCall(request, User::class.java, success, error)
}