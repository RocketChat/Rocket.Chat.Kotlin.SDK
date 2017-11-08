package chat.rocket.common.model

import com.squareup.moshi.Json

interface BaseUser {
    val username: String?

    enum class Status {
        @Json(name = "online")
        ONLINE,
        @Json(name = "busy")
        BUSY,
        @Json(name = "away")
        AWAY,
        @Json(name = "offline")
        OFFLINE
    }
}