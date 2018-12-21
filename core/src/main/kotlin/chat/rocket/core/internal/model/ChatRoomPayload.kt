package chat.rocket.core.internal.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ChatRoomPayload(@Json(name = "rid") val roomId: String)

@JsonSerializable
data class ChatRoomUnreadPayload(val roomId: String)

@JsonSerializable
data class ChatRoomNamePayload(val roomId: String, val name: String?)

@JsonSerializable
data class ChatRoomTopicPayload(val roomId: String, val topic: String?)

@JsonSerializable
data class ChatRoomDescriptionPayload(val roomId: String, val description: String?)

@JsonSerializable
data class ChatRoomAnnouncementPayload(val roomId: String, val announcement: String?)

@JsonSerializable
data class ChatRoomReadOnlyPayload(val roomId: String, val readOnly: Boolean)

@JsonSerializable
data class ChatRoomTypePayload(val roomId: String, val type: String)

@JsonSerializable
data class ChatRoomJoinCodePayload(val roomId: String, val joinCode: String)

@JsonSerializable
data class ChatRoomFavoritePayload(val roomId: String, val favorite: Boolean)