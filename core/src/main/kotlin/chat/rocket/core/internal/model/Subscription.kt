package chat.rocket.core.internal.model

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.BaseRoom
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.SimpleUser
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonDefaultValueBoolean
import se.ansman.kotshi.JsonDefaultValueLong
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
    @Json(name = "ls") @ISO8601Date val lastSeen: Long?,
    @Json(name = "_updatedAt") @ISO8601Date override val updatedAt: Long?,
    @Json(name = "default")
    @JsonDefaultValueBoolean(false)
    val isDefault: Boolean,
    @JsonDefaultValueBoolean(false)
    val open: Boolean,
    @JsonDefaultValueBoolean(false)
    val alert: Boolean,
    @JsonDefaultValueLong(0)
    val unread: Long,
    @JsonDefaultValueLong(0)
    val userMentions: Long?,
    @JsonDefaultValueLong(0)
    val groupMentions: Long?
) : BaseRoom {
    val lastModified: Long?
        get() = lastSeen
}