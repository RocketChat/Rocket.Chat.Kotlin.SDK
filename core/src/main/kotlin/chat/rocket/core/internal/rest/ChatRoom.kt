package chat.rocket.core.internal.rest

import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.model.ChatRoomPayload
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import okhttp3.RequestBody

/**
 * Marks a room as read.
 *
 * @param roomId The room to mark as read.
 */
suspend fun RocketChatClient.markAsRead(roomId: String) {
    withContext(CommonPool) {
        val payload = ChatRoomPayload(roomId)
        val adapter = moshi.adapter(ChatRoomPayload::class.java)
        val payloadBody = adapter.toJson(payload)

        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, "subscriptions.read").build()
        val request = requestBuilder(url).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}