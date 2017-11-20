package chat.rocket.core.model

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.BaseRoom
import chat.rocket.common.model.SimpleUser
import com.squareup.moshi.Json

data class Room(
        @Json(name = "_id") override val id: String,
        @Json(name = "t") override val type: BaseRoom.RoomType,
        @Json(name = "u") override val user: SimpleUser?,
        override val name: String?,
        @Json(name = "msgs") override val messageCount: Int,
        @Json(name = "ro") override val readonly: Boolean? = false,
        @Json(name = "ts") @ISO8601Date override val timestamp: Long,
        @Json(name = "lm") @ISO8601Date override val lastModified: Long?,
        @Json(name = "_updatedAt") @ISO8601Date override val updatedAt: Long?,
        override val default: Boolean? = false,
        override val topic: String?,
        override val announcement: String?
) : BaseRoom