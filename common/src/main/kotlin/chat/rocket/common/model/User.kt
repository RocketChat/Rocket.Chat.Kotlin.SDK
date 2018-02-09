package chat.rocket.common.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class User(
        @Json(name = "_id") val id: String,
        override val username: String?,
        override val name: String?,
        val status: UserStatus?,
        val utcOffset: Float?,
        val emails: List<Email>?
) : BaseUser