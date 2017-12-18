package chat.rocket.core.model

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.BaseRoom
import chat.rocket.common.model.SimpleUser
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Room(
        @Json(name = "_id") override val id: String,
        @Json(name = "t") override val type: BaseRoom.RoomType,
        @Json(name = "u") override val user: SimpleUser?,
        override val name: String?,
        @Json(name = "fname") override val fullName: String?,
        @Json(name = "ro") override val readonly: Boolean? = false,
        @Json(name = "_updatedAt") @ISO8601Date override val updatedAt: Long?,
        val topic: String?,
        val announcement: String?
) : BaseRoom