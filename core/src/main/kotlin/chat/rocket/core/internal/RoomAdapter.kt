package chat.rocket.core.internal

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.SimpleUser
import chat.rocket.core.model.Message
import chat.rocket.core.model.Room
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class RoomAdapter(moshi: Moshi) : JsonAdapter<Room>() {
    private val options = JsonReader.Options.of(
        "_id",
        "t",
        "u",
        "name",
        "fname",
        "ro",
        "_updatedAt",
        "topic",
        "description",
        "announcement",
        "lastMessage",
        "broadcast",
        "muted"
    )
    private val roomTypeAdapter = moshi.adapter<RoomType>(RoomType::class.java)
    private val simpleUserAdapter = moshi.adapter<SimpleUser>(SimpleUser::class.java)
    private val longAdapter = moshi.adapter<Long>(Long::class.java, ISO8601Date::class.java)
    private val messageAdapter: JsonAdapter<Message> = moshi.adapter<Message>(Message::class.java)
    private val stringListAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(List::class.java, String::class.java)
    )

    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: Room?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.beginObject()

        writer.name("_id")
        writer.value(value.id)
        writer.name("t")
        this.roomTypeAdapter.toJson(writer, value.type)
        writer.name("u")
        this.simpleUserAdapter.toJson(writer, value.user)
        writer.name("name")
        writer.value(value.name)
        writer.name("fname")
        writer.value(value.fullName)
        writer.name("ro")
        writer.value(value.readonly)
        writer.name("_updatedAt")
        this.longAdapter.toJson(writer, value.updatedAt)
        writer.name("topic")
        writer.value(value.topic)
        writer.name("description")
        writer.value(value.description)
        writer.name("announcement")
        writer.value(value.announcement)
        writer.name("lastMessage")
        this.messageAdapter.toJson(writer, value.lastMessage)
        writer.name("broadcast")
        writer.value(value.broadcast)
        writer.name("muted")
        this.stringListAdapter.toJson(writer, value.muted)

        writer.endObject()
    }

    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): Room? {
        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.nextNull<Room>()
        }

        reader.beginObject()

        lateinit var id: String
        lateinit var type: RoomType
        var user: SimpleUser? = null
        var name: String? = null
        var fullName: String? = null
        var readonly = false
        var updatedAt: Long? = null
        var topic: String? = null
        var description: String? = null
        var announcement: String? = null
        var lastMessage: Message? = null
        var broadcast = false
        var muted: List<String>? = null

        loop@ while (reader.hasNext()) {
            when (reader.selectName(options)) {
                0 -> {
                    id = reader.nextString()
                    continue@loop
                }
                1 -> {
                    roomTypeAdapter.fromJson(reader)?.let { type = it }
                    continue@loop
                }
                2 -> {
                    user = this.simpleUserAdapter.fromJson(reader)
                    continue@loop
                }
                3 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        name = reader.nextString()
                    }
                    continue@loop
                }
                4 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        fullName = reader.nextString()
                    }
                    continue@loop
                }
                5 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        readonly = reader.nextBoolean()
                    }
                    continue@loop
                }
                6 -> {
                    updatedAt = longAdapter.fromJson(reader)
                    continue@loop
                }
                7 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        topic = reader.nextString()
                    }
                    continue@loop
                }
                8 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        description = reader.nextString()
                    }
                    continue@loop
                }
                9 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        announcement = reader.nextString()
                    }
                    continue@loop
                }
                10 -> {
                    lastMessage = messageAdapter.fromJson(reader)
                    continue@loop
                }
                11 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull<Any>()
                    } else {
                        broadcast = reader.nextBoolean()
                    }
                    continue@loop
                }
                12 -> {
                    muted = stringListAdapter.fromJson(reader)
                    continue@loop
                }
                -1 -> {
                    reader.nextName()
                    reader.skipValue()
                    continue@loop
                }
            }
        }

        if (reader.peek() == JsonReader.Token.END_OBJECT) {
            reader.endObject()
        } else if (reader.peek() == JsonReader.Token.END_DOCUMENT) {
        }

        val stringBuilder: StringBuilder? = null
        if (stringBuilder != null) {
            throw NullPointerException(stringBuilder.toString())
        }

        return Room(
            id,
            type,
            user,
            name,
            fullName,
            readonly,
            updatedAt,
            topic,
            description,
            announcement,
            lastMessage,
            broadcast,
            muted
        )
    }
}

internal class RoomAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>?, moshi: Moshi): JsonAdapter<*>? {
        if (type is ParameterizedType) {
            val rawType = type.rawType
            if (rawType == List::class.java && type.actualTypeArguments[0] == Room::class.java) {
                return RoomAdapter(moshi)
            }
        }
        return null
    }
}