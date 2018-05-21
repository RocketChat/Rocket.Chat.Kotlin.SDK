package chat.rocket.core.model.attachment

import chat.rocket.common.model.User
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class GenericAttachment(
    @Json(name = "_id") val id: String?,
    val name: String?,
    val type: String?,
    val user: User?,
    val size: String?,
    val uploadedAt: String?,
    val url: String?
)