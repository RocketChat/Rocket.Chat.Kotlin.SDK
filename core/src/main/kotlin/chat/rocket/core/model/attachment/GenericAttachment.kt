package chat.rocket.core.model.attachment

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.User
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class GenericAttachment(
    @Json(name = "_id") val id: String,
    val name: String?,
    val type: String?,
    val size: String?,
    val user: User?,
    val path: String?,
    @ISO8601Date
    val uploadedAt: Long?
)