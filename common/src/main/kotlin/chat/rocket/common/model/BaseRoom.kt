package chat.rocket.common.model

import com.squareup.moshi.Json

interface BaseRoom {

    val id: String
    val type: RoomType
    val name: String?
    val user: SimpleUser?
    val messageCount: Int
    val readonly: Boolean?
    val timestamp: Long
    val lastModified: Long?
    val updatedAt: Long?
    val default: Boolean?
    val topic: String?
    val announcement: String?

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