package chat.rocket.common.model

import com.squareup.moshi.Json

interface BaseRoom {

    val roomType: RoomType?

    enum class RoomType {
        @Json(name = "c")
        PUBLIC,
        @Json(name = "p")
        PRIVATE,
        @Json(name = "d")
        ONE_TO_ONE,
        @Json(name = "l")
        LIVECHAT
    }
}