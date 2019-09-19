package chat.rocket.core.internal.rest

import chat.rocket.common.util.CalendarISO8601Converter
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestMultiResult
import chat.rocket.core.model.CustomEmoji
import chat.rocket.core.model.Removed
import com.squareup.moshi.Types

suspend fun RocketChatClient.getCustomEmojis(timestamp: Long? = null): RestMultiResult<List<CustomEmoji>, List<Removed>> {
    val urlBuilder = requestUrl(restUrl, "emoji-custom.list")
    timestamp?.let {
        val date = CalendarISO8601Converter().fromTimestamp(timestamp)
        urlBuilder.addQueryParameter("updatedSince", date)
    }

    val request = requestBuilderForAuthenticatedMethods(urlBuilder.build()).get().build()

    val type = Types.newParameterizedType(
        RestMultiResult::class.java,
        Types.newParameterizedType(List::class.java, CustomEmoji::class.java),
        Types.newParameterizedType(List::class.java, Removed::class.java)
    )
    return handleRestCall(request, type)
}
