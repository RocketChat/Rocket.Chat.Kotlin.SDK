package chat.rocket.common.model

import com.squareup.moshi.Json

data class User(
        @Json(name = "_id") val id: String,
        override val username: String?
) : BaseUser