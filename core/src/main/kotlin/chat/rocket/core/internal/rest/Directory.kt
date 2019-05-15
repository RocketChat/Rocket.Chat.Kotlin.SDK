package chat.rocket.core.internal.rest

import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.model.DirectoryResult
import chat.rocket.core.model.PagedResult
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun RocketChatClient.directory(
    text: String? = null,
    directoryRequestType: DirectoryRequestType,
    directoryWorkspaceType: DirectoryWorkspaceType = DirectoryWorkspaceType.Local(),
    offset: Long,
    count: Long
): PagedResult<List<DirectoryResult>> =
    withContext(Dispatchers.IO) {
        val query = if (text.isNullOrBlank()) {
            if (directoryRequestType is DirectoryRequestType.Users) {
                "{\"type\":\"${directoryRequestType.type}\",\"workspace\":\"${directoryWorkspaceType.type}\"}"
            } else {
                "{\"type\":\"${directoryRequestType.type}\"}"
            }
        } else {
            if (directoryRequestType is DirectoryRequestType.Users) {
                "{\"text\":\"$text\",\"type\":\"${directoryRequestType.type}\",\"workspace\":\"${directoryWorkspaceType.type}\"}"
            } else {
                "{\"text\":\"$text\",\"type\":\"${directoryRequestType.type}\"}"
            }
        }

        val sort = if (directoryRequestType is DirectoryRequestType.Channels) {
            "{\"usersCount\":-1}"
        } else {
            "{\"username\":1}"
        }

        val httpUrl = requestUrl(restUrl, "directory")
            .addQueryParameter("query", query)
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("count", count.toString())
            .addQueryParameter("sort", sort)
            .build()

        val request = requestBuilderForAuthenticatedMethods(httpUrl).get().build()

        val type = Types.newParameterizedType(
            RestResult::class.java,
            Types.newParameterizedType(List::class.java, DirectoryResult::class.java)
        )

        val result = handleRestCall<RestResult<List<DirectoryResult>>>(request, type)
        return@withContext PagedResult<List<DirectoryResult>>(
            result.result(), result.total() ?: 0, result.offset() ?: 0
        )
    }

sealed class DirectoryRequestType(val type: String) {
    class Users : DirectoryRequestType("users")
    class Channels : DirectoryRequestType("channels")
}

sealed class DirectoryWorkspaceType(val type: String) {
    class Local : DirectoryWorkspaceType("local")
    class All : DirectoryWorkspaceType("all")
}