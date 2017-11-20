package chat.rocket.core.model

import chat.rocket.common.model.BaseUser
import com.squareup.moshi.Json

data class Myself(
        @Json(name = "_id") val id: String,
        override val username: String?,
        val name: String?,
        val status: String?,
        val statusConnection: String?,
        val utcOffset: Float,
        val active: Boolean,
        val emails: List<Email>?
) : BaseUser