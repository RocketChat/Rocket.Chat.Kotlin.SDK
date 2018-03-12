package chat.rocket.core.internal.rest

import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.model.Command
import chat.rocket.core.model.PagedResult
import com.squareup.moshi.Types
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext

suspend fun RocketChatClient.commands(offset: Int = 0, count: Int = 0): PagedResult<List<Command>> = withContext(CommonPool) {
    val httpUrl = requestUrl(restUrl, "commands.list")
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("count", count.toString())
            .build()

    val request = requestBuilder(httpUrl).get().build()
    val type = Types.newParameterizedType(RestResult::class.java,
            Types.newParameterizedType(List::class.java, Command::class.java))
    val result = handleRestCall<RestResult<List<Command>>>(request, type)
    return@withContext PagedResult<List<Command>>(result.result(), (result.total()) ?: 0, result.offset() ?: 0)
}