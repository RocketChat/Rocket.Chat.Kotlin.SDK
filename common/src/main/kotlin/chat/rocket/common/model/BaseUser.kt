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