package chat.rocket.core.internal

import chat.rocket.core.model.Reactions
import com.squareup.moshi.*

internal class ReactionsAdapter : JsonAdapter<Reactions>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Reactions {
        val reactions = Reactions()
        if (reader.peek() == JsonReader.Token.BEGIN_ARRAY) {
            reader.beginArray()
            reader.endArray()
            return reactions
        }
        reader.beginObject()
        while (reader.hasNext()) {
            val usernames = mutableListOf<String>()
            val shortname = reader.nextName()
            reader.beginObject()
            if (reader.nextName() == "usernames") {
                reader.beginArray()
                while (reader.hasNext()) {
                    val username = reader.nextString()
                    usernames.add(username)
                }
                reader.endArray()
            }
            reader.endObject()
            reactions.put(shortname, usernames)
        }
        reader.endObject()
        return reactions
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Reactions?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}