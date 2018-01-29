package chat.rocket.core.internal.model

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.BaseRoom
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.SimpleUser
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonDefaultValueBoolean
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Subscription(
        @Json(name = "rid") val roomId: String,
        @Json(name = "_id") override val id: String,
        @Json(name = "t") override val type: RoomType,
        @Json(name = "u") override val user: SimpleUser?,
        val name: String,
        @Json(name = "fname") override val fullName: String?,
        @Json(name = "ro") override val readonly: Boolean? = false,
        @Json(name = "ts") @ISO8601Date val timestamp: Long?,
        @Json(name = "lm") @ISO8601Date val lastModified: Long?,
        @Json(name = "_updatedAt") @ISO8601Date override val updatedAt: Long?,
        @Json(name = "default")
        @JsonDefaultValueBoolean(false)
        val isDefault: Boolean,
        val open: Boolean,
        val alert: Boolean,
        val unread: Long,
        val userMentions: Long?,
        val groupMentions: Long?
) : BaseRoom