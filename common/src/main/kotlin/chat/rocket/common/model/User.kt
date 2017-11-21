package chat.rocket.common.model

import com.squareup.moshi.Json

data class User(
        @Json(name = "_id") val id: String,
        override val username: String?,
        val name: String?,
        val status: BaseUser.Status,
        val statusConnection: BaseUser.Status,
        val utcOffset: Float?,
        val active: Boolean,
        val success: Boolean,
        val emails: List<Email>?
) : BaseUser