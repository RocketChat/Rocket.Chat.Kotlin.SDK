package chat.rocket.common.model

import com.squareup.moshi.Json

interface BaseRoom {

    val id: String
    val type: RoomType
    val name: String?
    val fullName: String?
    val user: SimpleUser?
    val readonly: Boolean?
    val updatedAt: Long?

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