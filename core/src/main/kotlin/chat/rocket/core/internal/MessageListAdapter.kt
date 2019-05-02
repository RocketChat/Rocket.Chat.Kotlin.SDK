package chat.rocket.core.internal

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.SimpleRoom
import chat.rocket.common.model.SimpleUser
import chat.rocket.common.util.Logger
import chat.rocket.core.model.Message
import chat.rocket.core.model.MessageType
import chat.rocket.core.model.Reactions
import chat.rocket.core.model.attachment.Attachment
import chat.rocket.core.model.url.Url
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import se.ansman.kotshi.KotshiUtils
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class MessageListAdapter(moshi: Moshi, private val logger: Logger) : JsonAdapter<List<Message>>() {
    private val options = JsonReader.Options.of(
        "_id",
        "rid",
        "msg",
        "ts",
        "u",
        "_updatedAt",
        "editedAt",
        "editedBy",
        "alias",
        "avatar",
        "t",
        "groupable",
        "parseUrls",
        "urls",
        "mentions",
        "channels",
        "attachments",
        "pinned",
        "starred",
        "reactions",
        "role",
        "synced",
        "unread"
    )

    private val longAdapter = moshi.adapter<Long>(Long::class.java, ISO8601Date::class.java)
    private val simpleUserAdapter = moshi.adapter<SimpleUser>(SimpleUser::class.java)
    private val messageTypeAdapter = moshi.adapter<MessageType>(MessageType::class.java)
    private val urlListAdapter = moshi.adapter<List<Url>>(
        Types.newParameterizedType(List::class.java, Url::class.java)
    )
    private val simpleUserListAdapter = moshi.adapter<List<SimpleUser>>(
        Types.newParameterizedType(List::class.java, SimpleUser::class.java)
    )
    private val simpleRoomListAdapter = moshi.adapter<List<SimpleRoom>>(
        Types.newParameterizedType(List::class.java, SimpleRoom::class.java)
    )
    private val attachmentListAdapter = moshi.adapter<List<Attachment>>(
        Types.newParameterizedType(List::class.java, Attachment::class.java)
    )
    private val reactionsAdapter: JsonAdapter<Reactions> = moshi.adapter<Reactions>(Reactions::class.java)

    override fun toJson(writer: JsonWriter, messageList: List<Message>?) {
        if (messageList == null) {
            writer.nullValue()
            return
        }
        messageList.forEach { message ->
            writer.beginObject()

            writer.name("_id")
            writer.value(message.id)
            writer.name("rid")
            writer.value(message.roomId)
            writer.name("msg")
            writer.value(message.message)
            writer.name("ts")
            longAdapter.toJson(writer, message.timestamp)
            writer.name("u")
            simpleUserAdapter.toJson(writer, message.sender)
            writer.name("_updatedAt")
            longAdapter.toJson(writer, message.updatedAt)
            writer.name("editedAt")
            longAdapter.toJson(writer, message.editedAt)
            writer.name("editedBy")
            simpleUserAdapter.toJson(writer, message.editedBy)
            writer.name("alias")
            writer.value(message.senderAlias)
            writer.name("avatar")
            writer.value(message.avatar)
            writer.name("t")
            messageTypeAdapter.toJson(writer, message.type)
            writer.name("groupable")
            writer.value(message.groupable)
            writer.name("parseUrls")
            writer.value(message.parseUrls)
            writer.name("urls")
            urlListAdapter.toJson(writer, message.urls)
            writer.name("mentions")
            simpleUserListAdapter.toJson(writer, message.mentions)
            writer.name("channels")
            simpleRoomListAdapter.toJson(writer, message.channels)
            writer.name("attachments")
            attachmentListAdapter.toJson(writer, message.attachments)
            writer.name("pinned")
            writer.value(message.pinned)
            writer.name("starred")
            simpleUserListAdapter.toJson(writer, message.starred)
            writer.name("reactions")
            reactionsAdapter.toJson(writer, message.reactions)
            writer.name("role")
            writer.value(message.role)
            writer.name("synced")
            writer.value(message.synced)
            writer.name("unread")
            writer.value(message.unread)

            writer.endObject()
        }
    }

    override fun fromJson(reader: JsonReader): List<Message>? {
        val messageList = ArrayList<Message>()

        reader.beginArray()
        while (reader.hasNext()) {
            try {
                getMessage(reader)?.let { messageList.add(it) }
            } catch (exception: Exception) {
                logger.warn { "Exception while getting Message: $exception" }
            }
        }

        reader.endArray()
        return messageList
    }

    private fun getMessage(reader: JsonReader): Message? {
        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.nextNull<Message>()
        }

        reader.beginObject()

        lateinit var id: String
        lateinit var roomId: String
        var message = ""
        var timestamp: Long? = null
        var sender: SimpleUser? = null
        var updatedAt: Long? = null
        var editedAt: Long? = null
        var editedBy: SimpleUser? = null
        var senderAlias: String? = null
        var avatar: String? = null
        var type: MessageType? = null
        var groupable = false
        var parseUrls = false
        var urls: List<Url>? = null
        var mentions: List<SimpleUser>? = null
        var channels: List<SimpleRoom>? = null
        var attachments: List<Attachment>? = null
        var pinned = false
        var starred: List<SimpleUser>? = null
        var reactions: Reactions? = null
        var role: String? = null
        var synced = true
        var unread: Boolean? = null

        loop@ while (reader.hasNext()) {
            when (reader.selectName(options)) {
                0 -> {
                    id = reader.nextString()
                    continue@loop
                }
                1 -> {
                    roomId = reader.nextString()
                    continue@loop
                }
                2 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        message = reader.nextString()
                    }
                    continue@loop
                }
                3 -> {
                    timestamp = longAdapter.fromJson(reader)
                    continue@loop
                }
                4 -> {
                    sender = simpleUserAdapter.fromJson(reader)
                    continue@loop
                }
                5 -> {
                    updatedAt = longAdapter.fromJson(reader)
                    continue@loop
                }
                6 -> {
                    editedAt = longAdapter.fromJson(reader)
                    continue@loop
                }
                7 -> {
                    editedBy = simpleUserAdapter.fromJson(reader)
                    continue@loop
                }
                8 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        senderAlias = reader.nextString()
                    }
                    continue@loop
                }
                9 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        avatar = reader.nextString()
                    }
                    continue@loop
                }
                10 -> {
                    type = messageTypeAdapter.fromJson(reader)
                    continue@loop
                }
                11 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        groupable = reader.nextBoolean()
                    }
                    continue@loop
                }
                12 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        parseUrls = reader.nextBoolean()
                    }
                    continue@loop
                }
                13 -> {
                    urls = urlListAdapter.fromJson(reader)
                    continue@loop
                }
                14 -> {
                    mentions = simpleUserListAdapter.fromJson(reader)
                    continue@loop
                }
                15 -> {
                    channels = simpleRoomListAdapter.fromJson(reader)
                    continue@loop
                }
                16 -> {
                    attachments = attachmentListAdapter.fromJson(reader)
                    continue@loop
                }
                17 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        pinned = reader.nextBoolean()
                    }
                    continue@loop
                }
                18 -> {
                    starred = simpleUserListAdapter.fromJson(reader)
                    continue@loop
                }
                19 -> {
                    reactions = reactionsAdapter.fromJson(reader)
                    continue@loop
                }
                20 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        role = reader.nextString()
                    }
                    continue@loop
                }
                21 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        synced = reader.nextBoolean()
                    }
                    continue@loop
                }
                22 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        unread = reader.nextBoolean()
                    }
                    continue@loop
                }
                -1 -> {
                    if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                        break@loop
                    }
                    reader.nextName()
                    reader.skipValue()
                    continue@loop
                }
            }
        }

        if (reader.peek() != JsonReader.Token.BEGIN_OBJECT) {
            reader.endObject()
        }

        var stringBuilder: StringBuilder? = null
        if (timestamp == null) {
            stringBuilder = KotshiUtils.appendNullableError(stringBuilder, "timestamp")
        }
        if (stringBuilder != null) {
            throw NullPointerException(stringBuilder.toString())
        }

        return Message(
            id,
            roomId,
            message,
            timestamp!!,
            sender,
            updatedAt,
            editedAt,
            editedBy,
            senderAlias,
            avatar,
            type,
            groupable,
            parseUrls,
            urls,
            mentions,
            channels,
            attachments,
            pinned,
            starred,
            reactions,
            role,
            synced,
            unread
        )
    }
}

internal class MessageListAdapterFactory(private val logger: Logger) : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>?, moshi: Moshi): JsonAdapter<*>? {
        if (type is ParameterizedType) {
            val rawType = type.rawType
            if (rawType == List::class.java && type.actualTypeArguments[0] == Message::class.java) {
                return MessageListAdapter(moshi, logger)
            }
        }
        return null
    }
}
