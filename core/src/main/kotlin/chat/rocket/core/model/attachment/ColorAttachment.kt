package chat.rocket.core.model.attachment

import chat.rocket.common.internal.FallbackSealedClass
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ColorAttachment(
    val color: Color,
    val text: String,
    val fallback: String? = null,
    val fields: List<Field>? = null
) : Attachment {
    override val url: String
        get() = "#$color"
}

@FallbackSealedClass(name = "Custom", fieldName = "colorValue")
sealed class Color(val color: Int, val rawColor: String) {
    @Json(name = "good") class Good : Color(parseColor("#35AC19"), "#35AC19")
    @Json(name = "warning") class Warning : Color(parseColor("#FCB316"), "#FCB316")
    @Json(name = "danger") class Danger : Color(parseColor("#D30230"), "#D30230")
    class Custom(private val colorValue: String) : Color(parseColor(colorValue), colorValue)

    override fun toString(): String {
        return color.toString(16)
    }
}

private const val DEFAULT_COLOR_INT = 0xA0A0A0
const val DEFAULT_COLOR_STR = "#A0A0A0"
val DEFAULT_COLOR = Color.Custom(DEFAULT_COLOR_STR)

fun String?.asColorInt() = parseColor(this ?: DEFAULT_COLOR_STR)
fun String?.asColor() = Color.Custom(this ?: DEFAULT_COLOR_STR)

private fun parseColor(toParseColor: String): Int {
    var rawColor = toParseColor.toUpperCase()
    if (rawColor.startsWith("0X")) {
        rawColor = "#${rawColor.drop(2)}"
    }
    return if (rawColor.startsWith('#')) {
        var color = toParseColor.substring(1).toLong(16)
        if (toParseColor.length == 7) {
            // Set the alpha value
            color = color or -0x1000000
        } else if (toParseColor.length != 9) {
            color = DEFAULT_COLOR_INT.toLong()
        }
        color.toInt()
    } else {
        DEFAULT_COLOR_INT
    }
}