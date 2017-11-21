package chat.rocket.common.model

import com.squareup.moshi.Json

data class User(
        @Json(name = "_id") val id: String,
        override val username: String?,
        val status: BaseUser.Status,
        val statusConnection: BaseUser.Status,
        val utcOffset: Int,
        val active: Boolean,
        val success: Boolean,
        val emails: List<Email>?
) : BaseUser