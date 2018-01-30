package chat.rocket.core.internal.rest

import chat.rocket.common.model.BaseResult
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import com.squareup.moshi.Types
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext

/**
 * Logout user from the current logged-in server.
 */
suspend fun RocketChatClient.logout() = withContext(CommonPool) {
    val httpUrl = requestUrl(restUrl, "logout").build()
    val request = requestBuilder(httpUrl).get().build()

    handleRestCall<Any>(request, Any::class.java)
}