package chat.rocket.common.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class User(
    @Json(name = "_id") val id: String,
    override val username: String? = null,
    override val name: String? = null,
    val status: UserStatus? = null,
    val utcOffset: Float? = null,
    val emails: List<Email>? = null,
    val roles: List<String>? = null
) : BaseUser