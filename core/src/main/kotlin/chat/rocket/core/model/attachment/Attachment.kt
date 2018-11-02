package chat.rocket.core.model.attachment

import chat.rocket.common.internal.FallbackSealedClass
import chat.rocket.core.model.attachment.actions.Action
import com.squareup.moshi.Json

data class Attachment(
    var title: String? = null,
    var type: String? = null,
    var description: String? = null,
    var authorName: String? = null,
    var text: String? = null,
    var thumbUrl: String? = null,
    var color: Color? = null,
    var titleLink: String? = null,
    var titleLinkDownload: Boolean = false,
    var imageUrl: String? = null,
    var imageType: String? = null,
    var imageSize: Long? = null,
    var videoUrl: String? = null,
    var videoType: String? = null,
    var videoSize: Long? = null,
    var audioUrl: String? = null,
    var audioType: String? = null,
    var audioSize: Long? = null,
    var messageLink: String? = null,
    var attachments: List<Attachment>? = null,
    var timestamp: Long? = null,
    var authorIcon: String? = null,
    var authorLink: String? = null,
    var imagePreview: String? = null,
    var fields: List<Field>? = null,
    var fallback: String? = null,
    var buttonAlignment: String? = null,
    var actions: List<Action>? = null
)

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