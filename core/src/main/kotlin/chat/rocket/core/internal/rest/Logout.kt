package chat.rocket.core.internal.rest

import chat.rocket.core.RocketChatClient
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext

/**
 * Logout user from the current logged-in server.
 */
suspend fun RocketChatClient.logout() {
    withContext(CommonPool) {
        val httpUrl = requestUrl(restUrl, "logout").build()
        val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()

        handleRestCall<Any>(request, Any::class.java)
    }
}