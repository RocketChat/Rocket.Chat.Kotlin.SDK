package chat.rocket.core.model.attachment

import chat.rocket.common.internal.ISO8601Date
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class GenericAttachment(
    @Json(name = "_id") val id: String,
    val name: String?,
    val type: String?,
    val size: String?,
    val userId: String,
    val user: UserData,
    val path: String?,
    @ISO8601Date
    val uploadedAt: Long?
)

@JsonSerializable
data class UserData(val username: String?, val name: String?)