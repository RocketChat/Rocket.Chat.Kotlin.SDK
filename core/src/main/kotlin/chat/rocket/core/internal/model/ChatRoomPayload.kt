package chat.rocket.core.internal.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ChatRoomPayload(@Json(name = "rid") val roomId: String)

@JsonSerializable
data class ChatRoomJoinLeavePayload(val roomId: String)

@JsonSerializable
data class ChatRoomTopicPayload(
    val roomId: String,
    val topic: String
)

@JsonSerializable
data class ChatRoomDescriptionPayload(
    val roomId: String,
    val description: String
)

@JsonSerializable
data class ChatRoomAnnouncementPayload(
    val roomId: String,
    val announcement: String
)