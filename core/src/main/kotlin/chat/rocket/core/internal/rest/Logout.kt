package chat.rocket.core.internal.rest

import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.MessageResponse
import com.squareup.moshi.Types
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext

/**
 * Logout user from the current logged-in server.
 *
 * @return A result String message.
 */
suspend fun RocketChatClient.logout(): String? = withContext(CommonPool) {
    val httpUrl = requestUrl(restUrl, "logout").build()
    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(RestResult::class.java, MessageResponse::class.java)
    handleRestCall<RestResult<MessageResponse>>(request, type).result().message
}