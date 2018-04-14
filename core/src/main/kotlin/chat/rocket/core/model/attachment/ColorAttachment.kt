package chat.rocket.core.model.attachment

import chat.rocket.common.internal.FallbackSealedClass
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ColorAttachment(
    val color: Color,
    val text: String,
    val fallback: String? = null
) : Attachment {
    override val url: String
        get() = "#$color"
}

@FallbackSealedClass(name = "Custom", fieldName = "colorValue")
sealed class Color(val color: Int, val rawColor: String) {
    @Json(name = "good") class Good : Color(0x35AC19, "good")
    @Json(name = "warning") class Warning : Color(0xFCB316, "warning")
    @Json(name = "danger") class Danger : Color(0xD30230, "danger")
    class Custom(private val colorValue: String) : Color(parseColor(colorValue), colorValue)

    override fun toString(): String {
        return color.toString(16)
    }
}

private const val DEFAULT_COLOR = 0xA0A0A0
private fun parseColor(rawColor: String): Int {
    return if (rawColor.startsWith('#')) {
        var color = rawColor.substring(1).toLong(16)
        if (rawColor.length == 7) {
            // Set the alpha value
            color = color or -0x1000000
        } else if (rawColor.length != 9) {
            color = DEFAULT_COLOR.toLong()
        }
        color.toInt()
    } else {
        DEFAULT_COLOR
    }
}