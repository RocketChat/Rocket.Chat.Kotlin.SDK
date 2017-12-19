package chat.rocket.core.model

import chat.rocket.common.model.BaseUser
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Myself(
        @Json(name = "_id") val id: String,
        override val username: String?,
        override val name: String?,
        val status: BaseUser.Status?,
        val statusConnection: BaseUser.Status?,
        val utcOffset: Float,
        val active: Boolean,
        val emails: List<Email>?
) : BaseUser