package chat.rocket.core.internal.rest

import chat.rocket.common.model.BaseResult
import chat.rocket.common.model.RoomType
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.DeletePayload
import chat.rocket.core.internal.model.MessagePayload
import chat.rocket.core.internal.model.ReactionPayload
import chat.rocket.core.model.DeleteResult
import chat.rocket.core.model.Message
import chat.rocket.core.model.PagedResult
import chat.rocket.core.model.attachment.Attachment
import com.squareup.moshi.Types
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.InputStream

/**
 * Updates a message.
 *
 * @param roomId The room id of where the message is.
 * @param messageId The message id to update.
 * @param text Updated text for the message.
 * @return The updated Message object.
 */
suspend fun RocketChatClient.updateMessage(roomId: String, messageId: String, text: String): Message = withContext(CommonPool) {
    val payload = MessagePayload(roomId, text, null, null, null, null, messageId)
    val adapter = moshi.adapter(MessagePayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "chat.update").build()
    val request = requestBuilder(url).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, Message::class.java)
    return@withContext handleRestCall<RestResult<Message>>(request, type).result()
}

suspend fun RocketChatClient.pinMessage(messageId: String): Message = withContext(CommonPool) {
    val body = FormBody.Builder().add("messageId", messageId).build()

    val httpUrl = requestUrl(restUrl, "chat.pinMessage").build()

    val request = requestBuilder(httpUrl).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Message::class.java)

    return@withContext handleRestCall<RestResult<Message>>(request, type).result()
}

suspend fun RocketChatClient.unpinMessage(messageId: String) {
    withContext(CommonPool) {
        val body = FormBody.Builder().add("messageId", messageId).build()

        val httpUrl = requestUrl(restUrl, "chat.unPinMessage").build()

        val request = requestBuilder(httpUrl).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}

suspend fun RocketChatClient.getRoomFavoriteMessages(roomId: String,
                                                     roomType: RoomType,
                                                     offset: Int): PagedResult<List<Message>> = withContext(CommonPool) {
    val userId = tokenRepository.get()?.userId

    val httpUrl = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "messages"))
            .addQueryParameter("roomId", roomId)
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("query", "{\"starred._id\":{\"\$in\":[\"$userId\"]}}")
            .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Types.newParameterizedType(List::class.java, Message::class.java))
    val result = handleRestCall<RestResult<List<Message>>>(request, type)

    return@withContext PagedResult<List<Message>>(result.result(), result.total() ?: 0, result.offset() ?: 0)
}

suspend fun RocketChatClient.getRoomPinnedMessages(roomId: String,
                                                   roomType: RoomType,
                                                   offset: Int? = 0): PagedResult<List<Message>> = withContext(CommonPool) {
    val httpUrl = requestUrl(restUrl,
            getRestApiMethodNameByRoomType(roomType, "messages"))
            .addQueryParameter("roomId", roomId)
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("query", "{\"pinned\":true}")
            .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Types.newParameterizedType(List::class.java, Message::class.java))
    val result = handleRestCall<RestResult<List<Message>>>(request, type)

    return@withContext PagedResult<List<Message>>(result.result(), result.total() ?: 0, result.offset() ?: 0)
}

/**
 * Sends a new message
 *
 * @param roomId the room where to send the message (works with all types)
 * @param text Optional text message to send
 * @param alias Optianal alias to be used as the sender of the message
 * @param emoji Optional emoji to be used as the sender's avatar
 * @param avatar Optional avatar url to be used as the sender's avatar
 * @param attachments Optional List of [Attachment]
 * @return
 */
suspend fun RocketChatClient.sendMessage(roomId: String,
                                         text: String? = null,
                                         alias: String? = null,
                                         emoji: String? = null,
                                         avatar: String? = null,
                                         attachments: List<Attachment>? = null): Message = withContext(CommonPool) {
    val payload = MessagePayload(roomId, text, alias, emoji, avatar, attachments)
    val adapter = moshi.adapter(MessagePayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "chat.postMessage").build()
    val request = requestBuilder(url).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, Message::class.java)
    return@withContext handleRestCall<RestResult<Message>>(request, type).result()
}

/**
 * Uploads a file.
 *
 * @param roomId The room where to upload the file.
 * @param file The file to upload.
 * @param mimeType The MIME type of the file.
 * @param msg The message to send with the file.
 * @param description The file description.
 */
suspend fun RocketChatClient.uploadFile(roomId: String,
                                        file: File,
                                        mimeType: String,
                                        msg: String = "",
                                        description: String = "") {
    withContext(CommonPool) {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.name,
                        RequestBody.create(MediaType.parse(mimeType), file))
                .addFormDataPart("msg", msg)
                .addFormDataPart("description", description)
                .build()

        uploadFile(roomId, body)
    }
}

suspend fun RocketChatClient.uploadFile(roomId: String,
                                        fileName: String,
                                        mimeType: String,
                                        msg: String = "",
                                        description: String = "",
                                        inputStreamProvider: () -> InputStream?) {
    withContext(CommonPool) {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        InputStreamRequestBody(MediaType.parse(mimeType), inputStreamProvider))
                .addFormDataPart("msg", msg)
                .addFormDataPart("description", description)
                .build()

        uploadFile(roomId, body)
    }
}

private suspend fun RocketChatClient.uploadFile(roomId: String, body: RequestBody) {
    val httpUrl = requestUrl(restUrl, "rooms.upload")
            .addPathSegment(roomId)
            .build()
    val request = requestBuilder(httpUrl).post(body).build()

    handleRestCall<Any>(request, Any::class.java, largeFile = true)
}

suspend fun RocketChatClient.messages(roomId: String,
                                      roomType: RoomType,
                                      offset: Long,
                                      count: Long): PagedResult<List<Message>> = withContext(CommonPool) {
    val httpUrl = requestUrl(restUrl,
            getRestApiMethodNameByRoomType(roomType, "messages"))
            .addQueryParameter("roomId", roomId)
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("count", count.toString())
            .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Types.newParameterizedType(List::class.java, Message::class.java))
    val result = handleRestCall<RestResult<List<Message>>>(request, type)

    return@withContext PagedResult<List<Message>>(result.result(), result.total() ?: 0, result.offset() ?: 0)
}

/**
 * Deletes a message
 *
 * @param roomId the room id of where the message is to delete.
 * @param msgId The message id to delete.
 * @param asUser Whether the message should be deleted as the user who sent it. Defaults to false.
 */
suspend fun RocketChatClient.deleteMessage(roomId: String,
                                           msgId: String,
                                           asUser: Boolean = false): DeleteResult = withContext(CommonPool) {
    val payload = DeletePayload(roomId, msgId, asUser)
    val adapter = moshi.adapter(DeletePayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "chat.delete").build()
    val request = requestBuilder(url).post(body).build()

    return@withContext handleRestCall<DeleteResult>(request, DeleteResult::class.java)
}


suspend fun RocketChatClient.history(roomId: String,
                                     roomType: RoomType,
                                     count: Long = 50,
                                     oldest: String? = null,
                                     latest: String? = null): PagedResult<List<Message>> = withContext(CommonPool) {
    val httpUrl = requestUrl(restUrl, getRestApiMethodNameByRoomType(roomType, "history")).apply {
        addQueryParameter("roomId", roomId)
        addQueryParameter("count", count.toString())
        oldest?.let {
            addQueryParameter("oldest", it)
        }
        latest?.let {
            addQueryParameter("latest", it)
        }
    }.build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Types.newParameterizedType(List::class.java, Message::class.java))
    val result = handleRestCall<RestResult<List<Message>>>(request, type)

    return@withContext PagedResult<List<Message>>(result.result(), result.total() ?: -1, result.offset() ?: -1)
}

/**
 * Toggle a reaction to an associated message. If the message already has an associated :vulcan: reaction it will
 * clear it or else it will request server to add one.
 *
 * @param messageId The message id to reaction refers.
 * @param emoji The emoji to react with or clear.
 */
suspend fun RocketChatClient.toggleReaction(messageId: String, emoji: String): Boolean = withContext(CommonPool) {
    val url = requestUrl(restUrl, "chat.react").build()

    val payload = ReactionPayload(messageId, emoji)
    val adapter = moshi.adapter(ReactionPayload::class.java)
    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val request = requestBuilder(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}