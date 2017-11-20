package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.common.model.User
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
 * @param success lambda function receiving Myself
 * @param error lambda function for handling errors and exceptions
 * @see Myself
 * @see RocketChatException
 */
fun RocketChatClient.me(success: (Myself) -> Unit,
                        error: (RocketChatException) -> Unit) {
    val httpUrl = requestUrl(restUrl, "me").build()
    val request = requestBuilder(httpUrl).get().build()

    handleRestCall<Myself>(request, Myself::class.java, {
        success(it)
    }, error)
}

/**
 * List the public channel subscriptions for the current User
 *
 * @param offset optional offset
 * @param success (List<[Room]>, Long) lambda indicating success and total number of rooms
 * @param error ([RocketChatException]) lambda indicating errors
 * @see TokenRepository
 * @see Room
 * @see RocketChatException
 */
fun RocketChatClient.channelSubscriptions(offset: Long? = 0,
                                          success: (List<Room>, Long) -> Unit,
                                          error: (RocketChatException) -> Unit) {
    listSubscriptions("channels.list.joined", (offset ?: 0), success, error)
}

/**
 * List the private channel (groups) subscriptions for the current User
 *
 * @param offset optional offset
 * @param success (List<[Room]>, Long) lambda indicating success and total number of rooms
 * @param error ([RocketChatException]) lambda indicating errors
 * @see TokenRepository
 * @see Room
 * @see RocketChatException
 */
fun RocketChatClient.groupSubscriptions(offset: Long? = 0,
                                        success: (List<Room>, Long) -> Unit,
                                        error: (RocketChatException) -> Unit) {
    listSubscriptions("groups.list", (offset ?: 0), success, error)
}

/**
 * List the private message(dm) subscriptions for the current User
 *
 * @param offset optional offset
 * @param success (List<[Room]>, Long) lambda indicating success and total number of rooms
 * @param error ([RocketChatException]) lambda indicating errors
 * @see TokenRepository
 * @see Room
 * @see RocketChatException
 */
fun RocketChatClient.dmSubscriptions(offset: Long? = 0,
                                     success: (List<Room>, Long) -> Unit,
                                     error: (RocketChatException) -> Unit) {
    listSubscriptions("dm.list", (offset ?: 0), success, error)
}

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
}