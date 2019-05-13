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
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.skipValue()
            return reactions
        }
        reader.beginObject()
        while (reader.hasNext()) {
            val usernameList = mutableListOf<String>()
            val nameList = mutableListOf<String>()

            val nextName = reader.nextName()
            val shortname = if (nextName == "reactions") {
                reader.beginObject()
                reader.nextName()
            } else {
                nextName
            }

            reader.beginObject()
            if (reader.nextName() == "usernames") {
                reader.beginArray()
                while (reader.hasNext()) {
                    val username = reader.nextString()
                    usernameList.add(username)
                }
                reader.endArray()
            }
            if (reader.peek() != JsonReader.Token.END_OBJECT) {
                if (reader.nextName() == "names") {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        val name = reader.nextString()
                        nameList.add(name)
                    }
                    reader.endArray()
                }
            }

            reader.endObject()
            reactions.set(shortname, usernameList, nameList)
        }

        if (reader.peek() == JsonReader.Token.END_OBJECT) {
            reader.endObject()
        }
        if (reader.peek() == JsonReader.Token.END_OBJECT) {
            reader.endObject()
        }
        return reactions
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Reactions?) {
        if (value == null) {
            writer.nullValue()
        } else {
            with(writer) {
                beginObject()
                name("reactions")
                beginObject()
                value.getShortNames().forEach {
                    writeReaction(writer, it, value.getUsernames(it), value.getNames(it))
                }
                endObject()
                endObject()
            }
        }
    }

    private fun writeReaction(writer: JsonWriter, shortname: String, usernames: List<String>?, names: List<String>?) {
        with(writer) {
            name(shortname)
            beginObject()
            name("usernames")
            beginArray()
            usernames?.forEach { writer.value(it) }
            endArray()
            if (names != null && names.isNotEmpty()) {
                name("names")
                beginArray()
                names.forEach { writer.value(it) }
                endArray()
            }
            endObject()
        }
    }
}