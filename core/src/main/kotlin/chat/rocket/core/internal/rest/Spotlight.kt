package chat.rocket.core.internal.rest

import chat.rocket.core.RocketChatClient
import chat.rocket.core.model.SpotlightResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun RocketChatClient.spotlight(query: String): SpotlightResult = withContext(Dispatchers.IO) {
    val httpUrl = requestUrl(restUrl, "spotlight")
            .addQueryParameter("query", query)
            .build()

    val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()
    return@withContext handleRestCall<SpotlightResult>(request, SpotlightResult::class.java)
}