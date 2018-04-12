package chat.rocket.core.model.attachment

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonDefaultValueBoolean
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Field(
    val title: String,
    val value: String,
    @JsonDefaultValueBoolean(false)
    @Json(name = "short")
    val shortField: Boolean
)