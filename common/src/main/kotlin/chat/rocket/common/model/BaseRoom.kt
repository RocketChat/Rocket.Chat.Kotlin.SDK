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
    @Json(name = CHANNEL) class Channel : RoomType()
    @Json(name = PRIVATE_GROUP) class PrivateGroup : RoomType()
    @Json(name = DIRECT_MESSAGE) class DirectMessage : RoomType()
    @Json(name = LIVECHAT) class LiveChat : RoomType()
    class Custom(val rawType: String) : RoomType()

    override fun equals(other: Any?): Boolean {
        return other != null && other is RoomType && other.toString() == toString()
    }

    override fun toString(): String {
        return when (this) {
            is Channel -> CHANNEL
            is PrivateGroup -> PRIVATE_GROUP
            is DirectMessage -> DIRECT_MESSAGE
            is LiveChat -> LIVECHAT
            is Custom -> rawType
        }
    }

    companion object {
        const val CHANNEL = "c"
        const val PRIVATE_GROUP = "p"
        const val DIRECT_MESSAGE = "d"
        const val LIVECHAT = "l"
    }
}

fun RoomType.of(type: String): RoomType = roomTypeOf(type)

fun roomTypeOf(type: String): RoomType {
    return when (type) {
        RoomType.CHANNEL -> RoomType.Channel()
        RoomType.PRIVATE_GROUP -> RoomType.PrivateGroup()
        RoomType.DIRECT_MESSAGE -> RoomType.DirectMessage()
        RoomType.LIVECHAT -> RoomType.LiveChat()
        else -> RoomType.Custom(type)
    }
}