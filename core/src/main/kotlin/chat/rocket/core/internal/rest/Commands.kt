package chat.rocket.core.internal.rest

import chat.rocket.common.model.BaseResult
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.CommandPayload
import chat.rocket.core.model.Command
import chat.rocket.core.model.PagedResult
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody

suspend fun RocketChatClient.commands(offset: Int = 0, count: Int = 0): PagedResult<List<Command>> = withContext(Dispatchers.IO) {
    val httpUrl = requestUrl(restUrl, "commands.list")
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("count", count.toString())
            .build()

    val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()
    val type = Types.newParameterizedType(RestResult::class.java,
            Types.newParameterizedType(List::class.java, Command::class.java))
    val result = handleRestCall<RestResult<List<Command>>>(request, type)
    return@withContext PagedResult<List<Command>>(result.result(), (result.total()) ?: 0, result.offset() ?: 0)
}

suspend fun RocketChatClient.runCommand(command: Command, roomId: String): Boolean = withContext(Dispatchers.IO) {
    val httpUrl = requestUrl(restUrl, "commands.run")
            .build()

    val payload = CommandPayload(command.command, roomId, command.params)
    val adapter = moshi.adapter(CommandPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val request = requestBuilderForAuthenticatedMethods(httpUrl).post(body).build()
    return@withContext handleRestCall<BaseResult>(request, BaseResult::class.java).success
}