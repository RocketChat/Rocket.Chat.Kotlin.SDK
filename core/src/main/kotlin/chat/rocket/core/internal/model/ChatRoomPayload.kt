package chat.rocket.core.internal.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ChatRoomPayload(@Json(name = "rid") val roomId: String)

@JsonSerializable
data class ChatRoomJoinPayload(val roomId: String)