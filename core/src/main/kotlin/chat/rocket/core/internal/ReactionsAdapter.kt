package chat.rocket.core.internal

import chat.rocket.core.model.Reactions
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson
import com.squareup.moshi.JsonWriter

class ReactionsAdapter : JsonAdapter<Reactions>() {

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
        if (value == null) {
            writer.nullValue()
        } else {
            with(writer) {
                beginObject()
                value.getShortNames().forEach {
                    writeReaction(writer, it, value.getUsernames(it))
                }
                endObject()
            }
        }
    }

    private fun writeReaction(writer: JsonWriter, shortname: String, usernames: List<String>?) {
        with(writer) {
            name(shortname)
            beginObject()
            name("usernames")
            beginArray()
            usernames?.forEach {
                writer.value(it)
            }
            endArray()
            endObject()
        }
    }
}