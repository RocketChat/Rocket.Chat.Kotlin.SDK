package chat.rocket.core.internal.rest

import chat.rocket.core.RocketChatClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody

/**
 * Updates the jitsi timeout.
 *
 * REMARK Jitsi timeout needs to be called every 10 seconds to make sure the call is not ended and is available to web users.
 *
 * @param roomId the room id of where the jitsi timeout needs to be updated
 * @since Rocket.Chat Server version 0.74.0
 */
suspend fun RocketChatClient.updateJitsiTimeout(roomId: String) {
    withContext(Dispatchers.IO) {
        val body = FormBody.Builder().add("roomId", roomId).build()

        val httpUrl = requestUrlForVideoConference(restUrl, "jitsi.update-timeout").build()

        val request = requestBuilderForAuthenticatedMethods(httpUrl).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}
