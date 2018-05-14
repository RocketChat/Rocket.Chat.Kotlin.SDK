package chat.rocket.core.model

import chat.rocket.common.model.SimpleUser
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ChatRoomRole(
    @Json(name = "_id") val id: String,
    @Json(name = "rid") val roomId: String,
    @Json(name = "u") val user: SimpleUser,
    val roles: List<String>
)