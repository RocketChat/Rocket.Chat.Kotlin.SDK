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
    @Json(name = "c") class Public : RoomType()
    @Json(name = "p") class Private : RoomType()
    @Json(name = "d") class OneToOne: RoomType()
    @Json(name = "l") class Livechat : RoomType()
    class Custom(val rawType: String) : RoomType()

    companion object {
        val PUBLIC = Public()
        val PRIVATE = Private()
        val ONE_TO_ONE = OneToOne()
        val LIVECHAT = Livechat()
    }

    override fun toString(): String {
        return when (this) {
            is Public -> "c"
            is Private -> "p"
            is OneToOne -> "d"
            is Livechat -> "l"
            is Custom -> rawType
        }
    }
}

fun RoomType.of(type: String): RoomType = roomTypeOf(type)

fun roomTypeOf(type: String): RoomType {
    return when (type) {
        "c" -> RoomType.PUBLIC
        "p" -> RoomType.PRIVATE
        "d" -> RoomType.ONE_TO_ONE
        "l" -> RoomType.LIVECHAT
        else -> RoomType.Custom(type)
    }
}