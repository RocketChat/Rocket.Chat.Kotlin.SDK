package chat.rocket.common.model

import com.squareup.moshi.Json

data class SimpleRoom(
        @Json(name = "_id") val id: String,
        val name: String?
)