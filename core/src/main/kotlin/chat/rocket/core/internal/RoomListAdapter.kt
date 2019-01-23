package chat.rocket.core.internal

import chat.rocket.common.util.Logger
import chat.rocket.core.model.Room
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/*
 * This is a workaround for empty rooms object on api/v1/rooms.get
 *
 * this will just ignored Rooms causing NullPointerException with a specific message returned from
 * Kotshi generated Adapter.
 *
 * We are just filtering out this specific error to not mask other future bugs.
 *
 * TODO - convert to generic ListAdapter
 */
internal class RoomListAdapter(moshi: Moshi, private val logger: Logger) : JsonAdapter<List<Room>>() {

    private val adapter = moshi.adapter<Room>(Room::class.java)

    override fun toJson(writer: JsonWriter, value: List<Room>?) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun fromJson(reader: JsonReader): List<Room>? {
        val rooms = ArrayList<Room>()

        reader.beginArray()
        while (reader.hasNext()) {
            try {
                val room = adapter.fromJson(reader)
                room?.let {
                    rooms.add(room)
                }
            } catch (ex: Exception) {
                if (ex is NullPointerException && ex.message?.contains("The following properties were null") == true) {
                    logger.debug {
                        "Ignoring invalid room: ${reader.path}"
                    }
                    continue
                }
                throw ex
            }
        }
        reader.endArray()

        return rooms
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