package chat.rocket.core.internal.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class CreateNewChannelPayload(
    @Json(name = "name") val channelName: String,
    @Json(name = "members") val membersToInvite: List<String>?,
    val readOnly: Boolean?
)