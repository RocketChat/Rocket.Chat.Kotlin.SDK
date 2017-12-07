package chat.rocket.core.internal.model

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.BaseRoom
import chat.rocket.common.model.SimpleUser
import com.squareup.moshi.Json

data class Subscription(
        @Json(name = "rid") val roomId: String,
        @Json(name = "_id") override val id: String,
        @Json(name = "t") override val type: BaseRoom.RoomType,
        @Json(name = "u") override val user: SimpleUser?,
        override val name: String?,
        @Json(name = "fname") override val fullName: String?,
        @Json(name = "ro") override val readonly: Boolean? = false,
        @Json(name = "ts") @ISO8601Date val timestamp: Long?,
        @Json(name = "lm") @ISO8601Date val lastModified: Long?,
        @Json(name = "_updatedAt") @ISO8601Date override val updatedAt: Long?,
        val default: Boolean? = false,
        val open: Boolean,
        val alert: Boolean,
        val unread: Long,
        val userMentions: Long,
        val groupMentions: Long
) : BaseRoom