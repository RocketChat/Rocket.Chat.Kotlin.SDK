package chat.rocket.common.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SimpleUser(
        @Json(name = "_id") val id: String?,
        override val username: String?,
        override val name: String?
) : BaseUser