package chat.rocket.core.model

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.BaseMessage
import chat.rocket.common.model.User
import com.squareup.moshi.Json

data class Message(
        @Json(name = "_id") val id: String,
        @Json(name = "rid") override val roomId: String,
        @Json(name = "msg") override val message: String,
        @Json(name = "ts") @ISO8601Date override val timestamp: Long,
        @Json(name = "u") override val sender: User?,
        @Json(name = "_updatedAt") @ISO8601Date override val updatedAt: Long,
        @ISO8601Date override val editedAt: Long?,
        override val editedBy: User?,
        @Json(name = "alias") override val senderAlias: String?,
        val groupable: Boolean = false,
        val urls: List<Url>?
) : BaseMessage