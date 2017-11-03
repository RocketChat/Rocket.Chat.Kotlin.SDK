package chat.rocket.core.model

import chat.rocket.common.internal.BaseMessage
import com.squareup.moshi.Json

data class Message(
        @Json(name = "_id") override val id: String,
        @Json(name = "rid") override val roomId: String) : BaseMessage()