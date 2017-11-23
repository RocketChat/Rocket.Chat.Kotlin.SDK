package chat.rocket.core.model

import com.squareup.moshi.Json

data class Attachment(
        @Json(name = "_id") val id: String
)