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
    @Json(name = "prid") val parentId: String?, // Not empty if it is a discussion
    @Json(name = "_id") override val id: String,
    @Json(name = "t") override val type: RoomType,
    @Json(name = "u") override val user: SimpleUser?,
    val name: String?, // Name of the subscription
    @Json(name = "fname") override val fullName: String?, // Full name of the user, in the case of using the full user name setting (UI_Use_Real_Name)
    @Json(name = "ro") override val readonly: Boolean? = false,
    @Json(name = "ts") @ISO8601Date val timestamp: Long?,
    @Json(name = "ls") @ISO8601Date val lastSeen: Long?,
    @Json(name = "_updatedAt") @ISO8601Date override val updatedAt: Long?,
    val roles: List<String>?,
    @Json(name = "default")
    @JsonDefaultValueBoolean(false)
    val isDefault: Boolean,
    @Json(name = "f")
    @JsonDefaultValueBoolean(false)
    val isFavorite: Boolean,
    @JsonDefaultValueBoolean(false)
    val open: Boolean,
    @JsonDefaultValueBoolean(false)
    val alert: Boolean,
    @JsonDefaultValueBoolean(false)
    val archived: Boolean,
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