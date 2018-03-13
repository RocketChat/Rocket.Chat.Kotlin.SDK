package chat.rocket.core.internal

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.util.Logger
import chat.rocket.core.model.attachment.Attachment
import chat.rocket.core.model.attachment.AudioAttachment
import chat.rocket.core.model.attachment.ImageAttachment
import chat.rocket.core.model.attachment.MessageAttachment
import chat.rocket.core.model.attachment.VideoAttachment
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

class AttachmentAdapter(moshi: Moshi, private val logger: Logger) : JsonAdapter<Attachment>() {

    private val type = Types.newParameterizedType(List::class.java, Attachment::class.java)
    private val attachmentsAdapter = moshi.adapter<List<Attachment>>(type)
    private val tsAdapter = moshi.adapter<Long>(Long::class.java, ISO8601Date::class.java)

    private val NAMES = arrayOf(
            "title",                // 0
            "type",                 // 1
            "description",          // 2
            "author_name",          // 3
            "text",                 // 4
            "thumb_url",            // 5
            "color",                // 6
            "title_link",           // 7
            "title_link_download",  // 8
            "image_url",            // 9
            "image_type",           // 10
            "image_size",           // 11
            "video_url",            // 12
            "video_type",           // 13
            "video_size",           // 14
            "audio_url",            // 15
            "audio_type",           // 16
            "audio_size",           // 17
            "message_link",         // 18
            "attachments",          // 19
            "ts",                   // 20
            "author_icon",          // 21
            "author_link",          // 22
            "image_preview"         // 23
    )

    private val OPTIONS = JsonReader.Options.of(*NAMES)

    override fun fromJson(reader: JsonReader): Attachment? {
        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.nextNull<Attachment>()
        }

        var title: String? = null                 // 0
        var type: String? = null                  // 1
        var description: String? = null           // 2
        var author: String? = null                // 3
        var text: String? = null                  // 4
        var thumbUrl: String? = null              // 5
        var color: String? = null                 // 6
        var titleLink: String? = null             // 7
        var titleLinkDownload: Boolean = false    // 8
        var imageUrl: String? = null              // 9
        var imageType: String? = null             // 10
        var imageSize: Long? = null               // 11
        var videoUrl: String? = null              // 12
        var videoType: String? = null             // 13
        var videoSize: Long? = null               // 14
        var audioUrl: String? = null              // 15
        var audioType: String? = null             // 16
        var audioSize: Long? = null               // 17
        var messageLink: String? = null           // 18
        var attachments: List<Attachment>? = null // 19
        var timestamp: Long? = null               // 20
        var authorIcon: String? = null            // 21
        var authorLink: String? = null            // 22
        var imagePreview: String? = null          // 23

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.selectName(OPTIONS)) {
                0 -> title = reader.nextStringOrNull()
                1 -> type = reader.nextStringOrNull()
                2 -> description = reader.nextStringOrNull()
                3 -> author = reader.nextStringOrNull()
                4 -> text = reader.nextStringOrNull()
                5 -> thumbUrl = reader.nextStringOrNull()
                6 -> color = reader.nextStringOrNull()
                7 -> titleLink = reader.nextStringOrNull()
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
                18 -> messageLink = reader.nextStringOrNull()
                19 -> attachments = attachmentsAdapter.fromJson(reader)
                20 -> timestamp = tsAdapter.fromJson(reader)
                21 -> authorIcon = reader.nextStringOrNull()
                22 -> authorLink = reader.nextStringOrNull()
                23 -> imagePreview = reader.nextStringOrNull()
                else -> {
                    val name = reader.nextName()
                    logger.debug {
                        "Unknown/unmaped field at $name"
                    }
                    reader.skipValue()
                }
            }
        }
        reader.endObject()

        when {
            imageUrl != null -> {
                var preview: String? = null
                imagePreview?.let {
                    preview = "data:${imageType!!};base64,$it"
                }
                return ImageAttachment(title, description, titleLink, titleLinkDownload, imageUrl, imageType, imageSize, preview)
            }
            videoUrl != null -> {
                checkNonNull(videoType, "videoType")
                checkNonNull(videoSize, "videoSize")
                return VideoAttachment(title, description, titleLink, titleLinkDownload, videoUrl, videoType!!, videoSize!!)
            }
            audioUrl != null -> {
                checkNonNull(audioType, "audioType")
                checkNonNull(audioSize, "audioSize")
                return AudioAttachment(title, description, titleLink, titleLinkDownload, audioUrl, audioType!!, audioSize!!)
            }
            text != null -> {
                return MessageAttachment(author, authorIcon, text, thumbUrl, color, messageLink, attachments, timestamp)
            }/*
            authorLink != null -> {
                return MessageAttachment(title, author, authorIcon, text, thumbUrl, color, authorLink, attachments, timestamp)
            }*/
            else -> {
                logger.debug {
                    "Invalid Attachment type: supported are file and message at ${reader.path} - type: $type"
                }
                return null
                //throw JsonDataException("Invalid Attachment type: supported are image, video and audio")
            }
        }
    }

    override fun toJson(writer: JsonWriter, value: Attachment?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun checkNonNull(field: Any?, fieldName: String) {
        if (field == null) throw JsonDataException("$fieldName is null")
    }
}

class AttachmentAdapterFactory(private val logger: Logger) : JsonAdapter.Factory {
    override fun create(type: Type?, annotations: MutableSet<out Annotation>?, moshi: Moshi): JsonAdapter<*>? {
        type?.let {
            if (type == Attachment::class.java) {
                return AttachmentAdapter(moshi, logger)
            }
        }
        return null
    }
}

fun JsonReader.nextStringOrNull(): String? {
    if (peek() == JsonReader.Token.NULL) {
        skipValue()
        return null
    }
    return nextString()
}

fun JsonReader.nextLongOrNull(): Long? {
    if (peek() == JsonReader.Token.NULL) {
        skipValue()
        return null
    }
    return nextLong()
}

fun JsonReader.nextBooleanOrFalse(): Boolean {
    if (peek() == JsonReader.Token.NULL) {
        skipValue()
        return false
    }
    return nextBoolean()
}