package chat.rocket.common.model

import chat.rocket.common.internal.FallbackSealedClass
import com.squareup.moshi.Json

interface BaseUser {
    val username: String?
    val name: String?
}

@FallbackSealedClass(name = "Unknown", fieldName = "rawStatus")
sealed class UserStatus {
    @Json(name = "online")
    class Online : UserStatus()
    @Json(name = "busy")
    class Busy : UserStatus()
    @Json(name = "away")
    class Away : UserStatus()
    @Json(name = "offline")
    class Offline : UserStatus()

    class Unknown(val rawStatus: String) : UserStatus()

    override fun toString(): String {
        return when (this) {
            is Online -> "online"
            is Busy -> "busy"
            is Away -> "away"
            else -> "offline"
        }
    }
}

fun userStatusOf(status: String): UserStatus {
    return when(status) {
        "online" -> UserStatus.Online()
        "busy" -> UserStatus.Busy()
        "away" -> UserStatus.Away()
        "offline" -> UserStatus.Offline()
        else -> UserStatus.Unknown(status)
    }
}

@FallbackSealedClass(name = "Unknown", fieldName = "rawAvatar")
sealed class UserAvatar {
    @Json(name = "upload")
    class Upload : chat.rocket.common.model.UserAvatar()
    @Json(name = "url")
    class Url : chat.rocket.common.model.UserAvatar()
    class Cleared : chat.rocket.common.model.UserAvatar()
    class Unknown(val rawAvatar: String) : UserAvatar()

    override fun toString(): String {
        return when (this) {
            is Upload -> "upload"
            is Url -> "url"
            else -> "cleared"
        }
    }
}

fun userAvatarOf(avatar: String): UserAvatar {
    return when(avatar) {
        "upload" -> UserAvatar.Upload()
        "url" -> UserAvatar.Url()
        "cleared" -> UserAvatar.Cleared()
        else -> UserAvatar.Unknown(avatar)
    }
}
