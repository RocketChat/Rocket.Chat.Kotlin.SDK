package chat.rocket.common.model

import chat.rocket.common.internal.FallbackSealedClass
import com.squareup.moshi.Json

interface BaseRoom {
    val id: String
    val type: RoomType
    val fullName: String?
    val user: SimpleUser?
    val readonly: Boolean?
    val updatedAt: Long?
}

@FallbackSealedClass(name = "Custom", fieldName = "rawType")
sealed class RoomType {
    @Json(name = "c") class Channel : RoomType()
    @Json(name = "p") class PrivateGroup : RoomType()
    @Json(name = "d") class DirectMessage : RoomType()
    @Json(name = "l") class Livechat : RoomType()
    class Custom(val rawType: String) : RoomType()

    companion object {
        val CHANNEL = Channel()
        val PRIVATE_GROUP = PrivateGroup()
        val DIRECT_MESSAGE = DirectMessage()
        val LIVECHAT = Livechat()
    }

    override fun toString(): String {
        return when (this) {
            is Channel -> "c"
            is PrivateGroup -> "p"
            is DirectMessage -> "d"
            is Livechat -> "l"
            is Custom -> rawType
        }
    }
}

fun RoomType.of(type: String): RoomType = roomTypeOf(type)

fun roomTypeOf(type: String): RoomType {
    return when (type) {
        "c" -> RoomType.CHANNEL
        "p" -> RoomType.PRIVATE_GROUP
        "d" -> RoomType.DIRECT_MESSAGE
        "l" -> RoomType.LIVECHAT
        else -> RoomType.Custom(type)
    }
}