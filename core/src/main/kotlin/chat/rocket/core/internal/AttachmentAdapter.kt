package chat.rocket.core.internal

import chat.rocket.common.util.Logger
import chat.rocket.core.model.attachment.Attachment
import chat.rocket.core.model.attachment.AudioAttachment
import chat.rocket.core.model.attachment.ImageAttachment
import chat.rocket.core.model.attachment.VideoAttachment
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class AttachmentAdapter(private val logger: Logger) : JsonAdapter<Attachment>() {

    private val NAMES = arrayOf("title", "type", "description", "author", "text", "thumb_url", "color", "title_link",
            "title_link_download", "image_url", "image_type", "image_size", "video_url", "video_type", "video_size",
            "audio_url", "audio_type", "audio_size")
    private val OPTIONS = JsonReader.Options.of(*NAMES)

    override fun fromJson(reader: JsonReader): Attachment? {
        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.nextNull<Attachment>()
        }

        lateinit var title: String               // 0
        lateinit var type: String                // 1
        var description: String? = null          // 2
        var author: String? = null               // 3
        var text: String? = null                 // 4
        var thumbUrl: String? = null             // 5
        var color: String? = null                // 6
        lateinit var titleLink: String           // 7
        var titleLinkDownload: Boolean = false   // 8
        var imageUrl: String? = null             // 9
        var imageType: String? = null            // 10
        var imageSize: Long? = null              // 11
        var videoUrl: String? = null             // 12
        var videoType: String? = null            // 13
        var videoSize: Long? = null              // 14
        var audioUrl: String? = null             // 15
        var audioType: String? = null            // 16
        var audioSize: Long? = null              // 17

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.selectName(OPTIONS)) {
                0 -> title = reader.nextString()
                1 -> type = reader.nextString()
                2 -> description = reader.nextStringOrNull()
                3 -> author = reader.nextStringOrNull()
                4 -> text = reader.nextStringOrNull()
                5 -> thumbUrl = reader.nextStringOrNull()
                6 -> color = reader.nextStringOrNull()
                7 -> titleLink = reader.nextString()
                8 -> titleLinkDownload = reader.nextBooleanOrFalse()
                9 -> imageUrl = reader.nextStringOrNull()
                10 -> imageType = reader.nextStringOrNull()
                11 -> imageSize = reader.nextLongOrNull()
                12 -> videoUrl = reader.nextStringOrNull()
                13 -> videoType = reader.nextStringOrNull()
                14 -> videoSize = reader.nextLongOrNull()
                15 -> audioUrl = reader.nextStringOrNull()
                16 -> audioType = reader.nextStringOrNull()
                17 -> audioSize = reader.nextLongOrNull()
                else -> {
                    logger.debug {
                        "Unknown/unmaped field"
                    }
                    reader.skipValue()
                }
            }
        }
        reader.endObject()

        if (imageUrl != null) {
            if (description == null || imageType == null || imageSize == null) {
                throw JsonDataException("Missing fields for ImageAttachment")
            }
            return ImageAttachment(title, description, author, text, thumbUrl, color, titleLink, titleLinkDownload,
                    imageUrl, imageType, imageSize)
        } else if (videoUrl != null) {
            if (description == null || videoType == null || videoSize == null) {
                throw JsonDataException("Missing fields for AudioAttachment")
            }
            return VideoAttachment(title, description, author, text, thumbUrl, color, titleLink, titleLinkDownload,
                    videoUrl, videoType, videoSize)
        } else if (audioUrl != null) {
            if (description == null || audioType == null || audioSize == null) {
                throw JsonDataException("Missing fields for AudioAttachment")
            }
            return AudioAttachment(title, description, author, text, thumbUrl, color, titleLink, titleLinkDownload,
                    audioUrl, audioType, audioSize)
        } else {
            throw JsonDataException("Invalid Attachment type: supported are image, video and audio")
        }
    }

    override fun toJson(writer: JsonWriter, value: Attachment?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class AttachmentAdapterFactory(private val logger: Logger) : JsonAdapter.Factory {
    override fun create(type: Type?, annotations: MutableSet<out Annotation>?, moshi: Moshi?): JsonAdapter<*>? {
        type?.let {
            if (type == Attachment::class.java) {
                return AttachmentAdapter(logger)
            }
        }
        return null
    }
}

fun JsonReader.nextStringOrNull(): String? {
    if (peek() == JsonReader.Token.NULL) return null
    return nextString()
}

fun JsonReader.nextLongOrNull(): Long? {
    if (peek() == JsonReader.Token.NULL) return null
    return nextLong()
}

fun JsonReader.nextBooleanOrFalse(): Boolean {
    if (peek() == JsonReader.Token.NULL) return false
    return nextBoolean()
}