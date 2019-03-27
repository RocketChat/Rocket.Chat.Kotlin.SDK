package chat.rocket.core.internal.rest

import chat.rocket.core.RocketChatClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Logout user from the current logged-in server.
 */
suspend fun RocketChatClient.logout() {
    withContext(Dispatchers.IO) {
        val httpUrl = requestUrl(restUrl, "logout").build()
        val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()

        handleRestCall<Any>(request, Any::class.java)
    }
}