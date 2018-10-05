package chat.rocket.common.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SimpleUser(
        @Json(name = "_id") val id: String? = null,
        override val username: String? = null,
        override val name: String? = null
) : BaseUser