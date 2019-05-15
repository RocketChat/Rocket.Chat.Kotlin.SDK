package chat.rocket.core.internal

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.util.Logger
import chat.rocket.core.model.attachment.Attachment
import chat.rocket.core.model.attachment.Color
import chat.rocket.core.model.attachment.Field
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import chat.rocket.core.model.attachment.actions.Action
import chat.rocket.core.model.attachment.actions.ButtonAction
import java.lang.reflect.Type

class AttachmentAdapter(moshi: Moshi, private val logger: Logger) : JsonAdapter<Attachment>() {

    private val type = Types.newParameterizedType(List::class.java, Attachment::class.java)
    private val attachmentsAdapter = moshi.adapter<List<Attachment>>(type)
    private val tsAdapter = moshi.adapter<Long>(Long::class.java, ISO8601Date::class.java)
    private val fieldAdapter = moshi.adapter<Field>(Field::class.java)
    private val colorAdapter = moshi.adapter<Color>(Color::class.java)
    private val actionAdapter = moshi.adapter<ButtonAction>(ButtonAction::class.java)

    private val NAMES = arrayOf(
            "title", // 0
            "type", // 1
            "description", // 2
            "author_name", // 3
            "text", // 4
            "thumb_url", // 5
            "color", // 6
            "title_link", // 7
            "title_link_download", // 8
            "image_url", // 9
            "image_type", // 10
            "image_size", // 11
            "video_url", // 12
            "video_type", // 13
            "video_size", // 14
            "audio_url", // 15
            "audio_type", // 16
            "audio_size", // 17
            "message_link", // 18
            "attachments", // 19
            "ts", // 20
            "author_icon", // 21
            "author_link", // 22
            "image_preview", // 23
            "fields", // 24
            "fallback", // 25
            "button_alignment", // 26
            "actions" // 27
    )

    private val OPTIONS = JsonReader.Options.of(*NAMES)

    override fun fromJson(reader: JsonReader): Attachment? {
        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.nextNull<Attachment>()
        }

        var title: String? = null // 0
        var type: String? = null // 1
        var description: String? = null // 2
        var authorName: String? = null // 3
        var text: String? = null // 4
        var thumbUrl: String? = null // 5
        var color: Color? = null // 6
        var titleLink: String? = null // 7
        var titleLinkDownload = false // 8
        var imageUrl: String? = null // 9
        var imageType: String? = null // 10
        var imageSize: Long? = null // 11
        var videoUrl: String? = null // 12
        var videoType: String? = null // 13
        var videoSize: Long? = null // 14
        var audioUrl: String? = null // 15
        var audioType: String? = null // 16
        var audioSize: Long? = null // 17
        var messageLink: String? = null // 18
        var attachments: List<Attachment>? = null // 19
        var timestamp: Long? = null // 20
        var authorIcon: String? = null // 21
        var authorLink: String? = null // 22
        var imagePreview: String? = null // 23
        var fields: List<Field>? = null // 24
        var fallback: String? = null // 25
        var buttonAlignment: String? = null // 26
        var actions: List<Action>? = null // 27

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.selectName(OPTIONS)) {
                0 -> title = reader.nextStringOrNull()
                1 -> type = reader.nextStringOrNull()
                2 -> description = reader.nextStringOrNull()
                3 -> authorName = reader.nextStringOrNull()
                4 -> text = reader.nextStringOrNull()
                5 -> thumbUrl = reader.nextStringOrNull()
                6 -> color = colorAdapter.fromJson(reader)
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
                24 -> fields = parseFields(reader)
                25 -> fallback = reader.nextStringOrNull()
                26 -> buttonAlignment = reader.nextStringOrNull()
                27 -> actions = parseActions(reader)
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

        if (isAllNull(title, type, description, authorName, text, thumbUrl, color, titleLink, titleLinkDownload,
                        imageUrl, imageType, imageSize, videoUrl, videoType, videoSize, audioUrl, audioType, audioSize,
                        messageLink, attachments, timestamp, authorIcon, authorLink, imagePreview, fields, fallback,
                        buttonAlignment, actions)) {
            logger.debug {
                "Empty attachment"
            }
            return null
        }

        return Attachment(
            title = title,
            type = type,
            description = description,
            authorName = authorName,
            text = text,
            thumbUrl = thumbUrl,
            color = color,
            titleLink = titleLink,
            titleLinkDownload = titleLinkDownload,
            imageUrl = imageUrl,
            imageType = imageType,
            imageSize = imageSize,
            videoUrl = videoUrl,
            videoType = videoType,
            videoSize = videoSize,
            audioUrl = audioUrl,
            audioType = audioType,
            audioSize = audioSize,
            messageLink = messageLink,
            attachments = attachments,
            timestamp = timestamp,
            authorIcon = authorIcon,
            authorLink = authorLink,
            imagePreview = imagePreview,
            fields = fields,
            fallback = fallback,
            buttonAlignment = buttonAlignment,
            actions = actions
        )
    }

    private fun isAllNull(vararg params: Any?): Boolean {
        return params.isEmpty()
    }

    private fun parseFields(reader: JsonReader): List<Field>? {
        return when {
            reader.peek() == JsonReader.Token.NULL -> return reader.nextNull<List<Field>>()
            reader.peek() == JsonReader.Token.BEGIN_ARRAY -> {
                reader.beginArray()
                if (reader.peek() == JsonReader.Token.NULL) {
                    reader.skipValue()
                    reader.endArray()
                    return null
                }
                val list = ArrayList<Field>()
                while (reader.hasNext()) {
                    val field = fieldAdapter.fromJson(reader)
                    field?.let {
                        list.add(it)
                    }
                }
                reader.endArray()
                list
            }
            reader.peek() == JsonReader.Token.BEGIN_OBJECT -> {
                val list = ArrayList<Field>()
                reader.beginObject()
                val field = fieldAdapter.fromJson(reader)
                field?.let {
                    list.add(it)
                }
                reader.endObject()
                list
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    private fun parseActions(reader: JsonReader): List<Action>? {
        return when {
            reader.peek() == JsonReader.Token.NULL -> return reader.nextNull<List<Action>>()
            reader.peek() == JsonReader.Token.BEGIN_ARRAY -> {
                reader.beginArray()
                if (reader.peek() == JsonReader.Token.NULL) {
                    reader.skipValue()
                    reader.endArray()
                    return null
                }
                val list = ArrayList<Action>()
                while (reader.hasNext()) {
                    val action = actionAdapter.fromJson(reader)
                    action?.let {
                        list.add(it)
                    }
                }
                reader.endArray()
                list
            }
            reader.peek() == JsonReader.Token.BEGIN_OBJECT -> {
                val list = ArrayList<Action>()
                reader.beginObject()
                val action = actionAdapter.fromJson(reader)
                action?.let {
                    list.add(it)
                }
                reader.endObject()
                list
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    override fun toJson(writer: JsonWriter, attachment: Attachment?) {
        if (attachment == null) {
            writer.nullValue()
        } else {
            with(writer) {
                beginObject()
                name("title").value(attachment.title)
                name("description").value(attachment.description)
                name("text").value(attachment.text)
                name("title_link").value(attachment.titleLink)
                name("title_link_download").value(attachment.titleLinkDownload)
                name("image_url").value(attachment.imageUrl)
                name("image_size").value(attachment.imageSize)
                name("image_type").value(attachment.imageType)
                name("image_preview").value(attachment.imagePreview)
                name("video_url").value(attachment.videoUrl)
                name("video_size").value(attachment.videoType)
                name("video_type").value(attachment.videoUrl)
                name("audio_url").value(attachment.audioUrl)
                name("audio_size").value(attachment.audioSize)
                name("audio_type").value(attachment.audioType)
                name("author_link").value(attachment.authorLink)
                name("author_icon").value(attachment.authorIcon)
                name("author_name").value(attachment.authorName)
                name("button_alignment").value(attachment.buttonAlignment)
                name("color").value(attachment.color?.rawColor)
                name("fallback").value(attachment.fallback)
                name("thumbUrl").value(attachment.thumbUrl)
                name("message_link").value(attachment.messageLink)
                name("ts").value(attachment.timestamp)

                attachment.actions?.let { writeActions(writer, it) }
                attachment.fields?.let { writeFields(writer, it) }
            }
        }
    }

    private fun writeFields(writer: JsonWriter, fields: List<Field>) {
        if (fields.isNotEmpty()) {
            writer.name("fields")
            writer.beginArray()
            fields.forEach {
                writer.beginObject()
                writer.name("title").value(it.title)
                writer.name("value").value(it.value)
                writer.endObject()
            }
            writer.endArray()
        }
    }

    private fun writeActions(writer: JsonWriter, actions: List<Action>) {
        if (actions.isNotEmpty()) {
            writer.name("actions")
            writer.beginArray()
            actions.forEach {
                if (it is ButtonAction) {
                    writer.beginObject()
                    writer.name("type").value(it.type)
                    it.text?.let { writer.name("text").value(it) }
                    it.url?.let { writer.name("url").value(it) }
                    it.isWebView?.let { writer.name("is_webview").value(it) }
                    it.webViewHeightRatio?.let { writer.name("webview_height_ratio").value(it) }
                    it.imageUrl?.let { writer.name("image_url").value(it) }
                    it.message?.let { writer.name("msg").value(it) }
                    it.isMessageInChatWindow?.let { writer.name("msg_in_chat_window").value(it) }
                    writer.endObject()
                }
            }
            writer.endArray()
        }
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