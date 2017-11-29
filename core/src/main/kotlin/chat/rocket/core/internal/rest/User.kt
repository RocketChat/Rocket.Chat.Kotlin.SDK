package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import chat.rocket.core.internal.RestResult
import chat.rocket.core.model.Myself
import chat.rocket.core.model.Room
import com.squareup.moshi.Types

/**
 * Returns the current logged user information, useful to check if the Token from TokenProvider
 * is still valid.
 *
 * @see Myself
 * @see RocketChatException
 */
suspend fun RocketChatClient.me(): Myself {
    val httpUrl = requestUrl(restUrl, "me").build()
    val request = requestBuilder(httpUrl).get().build()

    return handleRestCall(request, Myself::class.java)
}

/*
internal fun RocketChatClient.listSubscriptions(method: String, offset: Long,
                                                success: (List<Room>, Long) -> Unit,
                                                error: (RocketChatException) -> Unit) {
    val httpUrl = requestUrl(restUrl, method)
            .addQueryParameter("offset", offset.toString())
            .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Types.newParameterizedType(List::class.java, Room::class.java))
    handleRestCall<RestResult<List<Room>>>(request, type, {
        success(it.result(), it.total() ?: it.result().size.toLong())
    }, error)
}*/
