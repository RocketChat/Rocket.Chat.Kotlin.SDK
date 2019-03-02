package chat.rocket.core.model

import chat.rocket.common.model.BaseUser
import chat.rocket.common.model.UserStatus
import chat.rocket.common.model.UserAvatar
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Myself(
    @Json(name = "_id") val id: String,
    val active: Boolean?,
    override val username: String?,
    override val name: String?,
    val status: UserStatus?,
    val statusConnection: UserStatus?,
    val statusDefault: UserStatus?,
    val avatarOrigin: UserAvatar?,
    val utcOffset: Float?,
    val emails: List<Email>?,
    val roles: List<String>?
) : BaseUser