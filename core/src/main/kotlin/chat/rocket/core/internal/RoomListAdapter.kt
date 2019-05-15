package chat.rocket.core.internal

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.SimpleUser
import chat.rocket.common.util.Logger
import chat.rocket.core.model.Message
import chat.rocket.core.model.Room
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.StringBuilder
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/*
 * This is a workaround for empty rooms object on api/v1/rooms.get
 *
 * This will just ignored Rooms causing NullPointerException with a specific message returned from
 * Kotshi generated Adapter.
 *
 * We are just filtering out this specific error to not mask other future bugs.
 *
 * TODO - convert to generic ListAdapter
 */
internal class RoomListAdapter(moshi: Moshi, private val logger: Logger) : JsonAdapter<List<Room>>() {
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
    private val messageAdapter = moshi.adapter<Message>(Message::class.java)
    private val stringListAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(List::class.java, String::class.java)
    )

    override fun toJson(writer: JsonWriter, roomList: List<Room>?) {
        if (roomList == null) {
            writer.nullValue()
            return
        }

        roomList.forEach { room ->
            writer.beginObject()

            writer.name("_id")
            writer.value(room.id)
            writer.name("t")
            roomTypeAdapter.toJson(writer, room.type)
            writer.name("u")
            simpleUserAdapter.toJson(writer, room.user)
            writer.name("name")
            writer.value(room.name)
            writer.name("fname")
            writer.value(room.fullName)
            writer.name("ro")
            writer.value(room.readonly)
            writer.name("_updatedAt")
            longAdapter.toJson(writer, room.updatedAt)
            writer.name("topic")
            writer.value(room.topic)
            writer.name("description")
            writer.value(room.description)
            writer.name("announcement")
            writer.value(room.announcement)
            writer.name("lastMessage")
            messageAdapter.toJson(writer, room.lastMessage)
            writer.name("broadcast")
            writer.value(room.broadcast)
            writer.name("muted")
            stringListAdapter.toJson(writer, room.muted)

            writer.endObject()
        }
    }

    override fun fromJson(reader: JsonReader): List<Room>? {
        val roomList = ArrayList<Room>()

        reader.beginArray()
        while (reader.hasNext()) {
            try {
                getRoom(reader)?.let { roomList.add(it) }
            } catch (exception: Exception) {
                if (exception is NullPointerException &&
                    exception.message?.contains("The following properties were null") == true
                ) {
                    logger.warn { "Ignoring invalid room: ${reader.path}" }
                    continue
                }
                throw exception
            }
        }
        reader.endArray()
        return roomList
    }

    private fun getRoom(reader: JsonReader): Room? {
        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.nextNull()
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
                    user = simpleUserAdapter.fromJson(reader)
                    continue@loop
                }
                3 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull()!!
                    } else {
                        name = reader.nextString()
                    }
                    continue@loop
                }
                4 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull()!!
                    } else {
                        fullName = reader.nextString()
                    }
                    continue@loop
                }
                5 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull()!!
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
                        reader.nextNull()!!
                    } else {
                        topic = reader.nextString()
                    }
                    continue@loop
                }
                8 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull()!!
                    } else {
                        description = reader.nextString()
                    }
                    continue@loop
                }
                9 -> {
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.nextNull()!!
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
                        reader.nextNull()!!
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

        val stringBuilder: StringBuilder? = null
        if (stringBuilder != null) {
            throw java.lang.NullPointerException(stringBuilder.toString())
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

internal class RoomListAdapterFactory(private val logger: Logger) : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>?, moshi: Moshi): JsonAdapter<*>? {
        if (type is ParameterizedType) {
            val rawType = type.rawType
            if (rawType == List::class.java && type.actualTypeArguments[0] == Room::class.java) {
                return RoomListAdapter(moshi, logger)
            }
        }
        return null
    }
}