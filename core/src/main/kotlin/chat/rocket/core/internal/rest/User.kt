package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.model.Myself
import chat.rocket.core.model.Room
import com.squareup.moshi.Types

/**
 *
 */
fun RocketChatClient.me(success: (Myself) -> Unit,
                        error: (RocketChatException) -> Unit) {
    val httpUrl = requestUrl(restUrl, "me").build()
    val request = requestBuilder(httpUrl).get().build()

    handleRestCall<Myself>(request, Myself::class.java, {
        success.invoke(it)
    }, error)
}

fun RocketChatClient.channelSubscriptions(offset: Long? = 0,
                                          success: (List<Room>, Long) -> Unit,
                                          error: (RocketChatException) -> Unit) {
    val httpUrl = requestUrl(restUrl, "channels.list.joined")
            .addQueryParameter("offset", offset.toString())
            .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Types.newParameterizedType(List::class.java, Room::class.java))
    handleRestCall<RestResult<List<Room>>>(request, type, {
        success.invoke(it.result(), it.total() ?: it.result().size.toLong())
    }, error)
}

fun RocketChatClient.groupSubscriptions(offset: Long? = 0,
                                        success: (List<Room>, Long) -> Unit,
                                        error: (RocketChatException) -> Unit) {
    val httpUrl = requestUrl(restUrl, "groups.list")
            .addQueryParameter("offset", offset.toString())
            .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Types.newParameterizedType(List::class.java, Room::class.java))
    handleRestCall<RestResult<List<Room>>>(request, type, {
        success.invoke(it.result(), it.total() ?: it.result().size.toLong())
    }, error)
}

fun RocketChatClient.dmSubscriptions(offset: Long? = 0,
                                        success: (List<Room>, Long) -> Unit,
                                        error: (RocketChatException) -> Unit) {
    val httpUrl = requestUrl(restUrl, "dm.list")
            .addQueryParameter("offset", offset.toString())
            .build()

    val request = requestBuilder(httpUrl).get().build()

    val type = Types.newParameterizedType(RestResult::class.java,
            Types.newParameterizedType(List::class.java, Room::class.java))
    handleRestCall<RestResult<List<Room>>>(request, type, {
        success.invoke(it.result(), it.total() ?: it.result().size.toLong())
    }, error)
}