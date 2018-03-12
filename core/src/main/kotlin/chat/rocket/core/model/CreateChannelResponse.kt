package chat.rocket.core.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class CreateChannelResponse(
        @Json(name = "channel") val room: Room,
        @Json(name = "success") val status: Boolean)
