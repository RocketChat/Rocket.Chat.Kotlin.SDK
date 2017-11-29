package chat.rocket.core.internal.rest

import chat.rocket.common.model.ServerInfo
import chat.rocket.core.RocketChatClient
import okhttp3.Request

suspend fun RocketChatClient.serverInfo(): ServerInfo {
    val url = restUrl.newBuilder()
            .addPathSegment("api")
            .addPathSegment("info")
            .build()

    val request = Request.Builder().url(url).get().build()

    return handleRestCall(request, ServerInfo::class.java)
}