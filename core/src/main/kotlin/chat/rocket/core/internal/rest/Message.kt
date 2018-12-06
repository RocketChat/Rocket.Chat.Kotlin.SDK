package chat.rocket.core.internal.rest

import chat.rocket.common.model.BaseResult
import chat.rocket.common.model.RoomType
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.CreateDirectMessagePayload
import chat.rocket.core.internal.model.DeletePayload
import chat.rocket.core.internal.model.MessageReportPayload
import chat.rocket.core.internal.model.PostMessagePayload
import chat.rocket.core.internal.model.ReactionPayload
import chat.rocket.core.internal.model.SendMessageBody
import chat.rocket.core.internal.model.SendMessagePayload
import chat.rocket.core.model.DeleteResult
import chat.rocket.core.model.Message
import chat.rocket.core.model.NewDirectMessageResult
import chat.rocket.core.model.PagedResult
import chat.rocket.core.model.ReadReceipt
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
 * Sends a new message with the given message id.
 *
 * @param messageId The id of message.
 * @param roomId The room where to send the message (works with all types).
 * @param message Optional text message to send.
 * @param alias Optional alias to be used as the sender of the message.
 * @param emoji Optional emoji to be used as the sender's avatar.
 * @param avatar Optional avatar url to be used as the sender's avatar.
 * @param attachments Optional List of [Attachment].
 * @return The message object.
 */
suspend fun RocketChatClient.sendMessage(
    messageId: String,
    roomId: String,
    message: String? = null,
    alias: String? = null,
    emoji: String? = null,
    avatar: String? = null,
    attachments: List<Attachment>? = null
): Message = withContext(CommonPool) {
    val payload = SendMessagePayload(
        SendMessageBody(messageId, roomId, message, alias, emoji, avatar, attachments)
    )
    val adapter = moshi.adapter(SendMessagePayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "chat.sendMessage").build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, Message::class.java)
    return@withContext handleRestCall<RestResult<Message>>(request, type).result()
}

/**
 * Posts a new message.
 *
 * @param roomId The room where to send the message (works with all types).
 * @param text Optional text message to send.
 * @param alias Optional alias to be used as the sender of the message.
 * @param emoji Optional emoji to be used as the sender's avatar.
 * @param avatar Optional avatar url to be used as the sender's avatar.
 * @param attachments Optional List of [Attachment].
 * @return The message object.
 */
suspend fun RocketChatClient.postMessage(
    roomId: String,
    text: String? = null,
    alias: String? = null,
    emoji: String? = null,
    avatar: String? = null,
    attachments: List<Attachment>? = null
): Message = withContext(CommonPool) {
    val payload = PostMessagePayload(roomId, text, alias, emoji, avatar, attachments)
    val adapter = moshi.adapter(PostMessagePayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "chat.postMessage").build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, Message::class.java)
    return@withContext handleRestCall<RestResult<Message>>(request, type).result()
}

/**
 * Updates a message.
 *
 * @param roomId The room id of where the message is.
 * @param messageId The message id to update.
 * @param text Updated text for the message.
 * @return The updated Message object.
 */
suspend fun RocketChatClient.updateMessage(roomId: String, messageId: String, text: String): Message =
    withContext(CommonPool) {
        val payload = PostMessagePayload(roomId, text, null, null, null, null, messageId)
        val adapter = moshi.adapter(PostMessagePayload::class.java)
        val payloadBody = adapter.toJson(payload)

        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, "chat.update").build()
        val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

        val type = Types.newParameterizedType(RestResult::class.java, Message::class.java)

        return@withContext handleRestCall<RestResult<Message>>(request, type).result()
    }

/**
 * Deletes a message.
 *
 * @param roomId the room id of where the message is to delete.
 * @param msgId The message id to delete.
 * @param asUser Whether the message should be deleted as the user who sent it. Defaults to false.
 */
suspend fun RocketChatClient.deleteMessage(
    roomId: String,
    msgId: String,
    asUser: Boolean = false
): DeleteResult = withContext(CommonPool) {
    val payload = DeletePayload(roomId, msgId, asUser)
    val adapter = moshi.adapter(DeletePayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "chat.delete").build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<DeleteResult>(request, DeleteResult::class.java)
}

/**
 * Stars a chat message for the authenticated user.
 *
 * @param messageId The message id to star.
 */
suspend fun RocketChatClient.starMessage(messageId: String) {
    withContext(CommonPool) {
        val body = FormBody.Builder().add("messageId", messageId).build()

        val httpUrl = requestUrl(restUrl, "chat.starMessage").build()

        val request = requestBuilderForAuthenticatedMethods(httpUrl).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}

/**
 * Removes the star on the chat message for the authenticated user.
 *
 * @param messageId The message id to unstar.
 */
suspend fun RocketChatClient.unstarMessage(messageId: String) {
    withContext(CommonPool) {
        val body = FormBody.Builder().add("messageId", messageId).build()

        val httpUrl = requestUrl(restUrl, "chat.unStarMessage").build()

        val request = requestBuilderForAuthenticatedMethods(httpUrl).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}

/**
 * Pins a chat message to the message’s channel.
 *
 * @param messageId The message id to pin.
 */
suspend fun RocketChatClient.pinMessage(messageId: String) {
    withContext(CommonPool) {
        val body = FormBody.Builder().add("messageId", messageId).build()

        val httpUrl = requestUrl(restUrl, "chat.pinMessage").build()

        val request = requestBuilderForAuthenticatedMethods(httpUrl).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
}

/**
 * Removes the pinned status of the provided chat message.
 *
 * @param messageId The message id to unpin.
 */
suspend fun RocketChatClient.unpinMessage(messageId: String) {
    withContext(CommonPool) {
        val body = FormBody.Builder().add("messageId", messageId).build()

        val httpUrl = requestUrl(restUrl, "chat.unPinMessage").build()

        val request = requestBuilderForAuthenticatedMethods(httpUrl).post(body).build()

        handleRestCall<Any>(request, Any::class.java)
    }
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

    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
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
suspend fun RocketChatClient.uploadFile(
    roomId: String,
    file: File,
    mimeType: String,
    msg: String = "",
    description: String = ""
) {
    withContext(CommonPool) {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", file.name,
                RequestBody.create(MediaType.parse(mimeType), file)
            )
            .addFormDataPart("msg", msg)
            .addFormDataPart("description", description)
            .build()

        uploadFile(roomId, body)
    }
}

suspend fun RocketChatClient.uploadFile(
    roomId: String,
    fileName: String,
    mimeType: String,
    msg: String = "",
    description: String = "",
    inputStreamProvider: () -> InputStream?
) {
    withContext(CommonPool) {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", fileName,
                InputStreamRequestBody(MediaType.parse(mimeType), inputStreamProvider)
            )
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
    val request = requestBuilderForAuthenticatedMethods(httpUrl).post(body).build()

    handleRestCall<Any>(request, Any::class.java, largeFile = true)
}

suspend fun RocketChatClient.messages(
    roomId: String,
    roomType: RoomType,
    offset: Long,
    count: Long
): PagedResult<List<Message>> = withContext(CommonPool) {
    val httpUrl = requestUrl(
        restUrl,
        getRestApiMethodNameByRoomType(roomType, "messages")
    )
        .addQueryParameter("roomId", roomId)
        .addQueryParameter("offset", offset.toString())
        .addQueryParameter("count", count.toString())
        .build()

    val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()

    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, Message::class.java)
    )
    val result = handleRestCall<RestResult<List<Message>>>(request, type)

    return@withContext PagedResult<List<Message>>(result.result(), result.total() ?: 0, result.offset() ?: 0)
}

suspend fun RocketChatClient.history(
    roomId: String,
    roomType: RoomType,
    count: Long = 50,
    oldest: String? = null,
    latest: String? = null
): PagedResult<List<Message>> = withContext(CommonPool) {
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

    val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()

    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, Message::class.java)
    )
    val result = handleRestCall<RestResult<List<Message>>>(request, type)

    return@withContext PagedResult<List<Message>>(result.result(), result.total() ?: -1, result.offset() ?: -1)
}

suspend fun RocketChatClient.getMessageReadReceipts(
    messageId: String,
    count: Long = 50,
    oldest: String? = null,
    latest: String? = null
): PagedResult<List<ReadReceipt>> = withContext(CommonPool) {
    val httpUrl = requestUrl(restUrl, "chat.getMessageReadReceipts").apply {
        addQueryParameter("messageId", messageId)
        addQueryParameter("count", count.toString())
        oldest?.let {
            addQueryParameter("oldest", it)
        }
        latest?.let {
            addQueryParameter("latest", it)
        }
    }.build()

    val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()

    val type = Types.newParameterizedType(
        RestResult::class.java,
        Types.newParameterizedType(List::class.java, ReadReceipt::class.java)
    )
    val result = handleRestCall<RestResult<List<ReadReceipt>>>(request, type)

    return@withContext PagedResult<List<ReadReceipt>>(result.result(), result.total() ?: -1, result.offset() ?: -1)
}

/**
 * Reports a message identified by {messageId} along with its {description}.
 *
 * @param messageId The id of the message.
 * @param description The description of the message being reported.
 */
suspend fun RocketChatClient.reportMessage(
    messageId: String,
    description: String
): Boolean = withContext(CommonPool) {
    val payload = MessageReportPayload(messageId = messageId, description = description)
    val adapter = moshi.adapter(MessageReportPayload::class.java)
    val payloadBody = adapter.toJson(payload)

    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "chat.reportMessage").build()
    val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}

/**
 * Create a direct message session with another user.
 *
 * @param username The username of the user to create a session with.
 * @return The updated Message object.
 */
suspend fun RocketChatClient.createDirectMessage(username: String): NewDirectMessageResult =
    withContext(CommonPool) {
        val payload = CreateDirectMessagePayload(username = username)
        val adapter = moshi.adapter(CreateDirectMessagePayload::class.java)
        val payloadBody = adapter.toJson(payload)

        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val url = requestUrl(restUrl, "im.create").build()
        val request = requestBuilderForAuthenticatedMethods(url).post(body).build()

        val type = Types.newParameterizedType(RestResult::class.java, NewDirectMessageResult::class.java)

        return@withContext handleRestCall<RestResult<NewDirectMessageResult>>(request, type).result()
    }
