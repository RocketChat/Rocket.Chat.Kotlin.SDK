package chat.rocket.core.internal

import chat.rocket.core.model.Reaction
import com.squareup.moshi.*

class ReactionsAdapter : JsonAdapter<List<Reaction>>() {

    @FromJson
    override fun fromJson(reader: JsonReader): List<Reaction> {
        val reactions = mutableListOf<Reaction>()
        reader.beginObject()
        while (reader.hasNext()) {
            val usernames = mutableListOf<String>()
            val name = reader.nextName()
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
            reactions.add(Reaction(name, usernames))
        }
        reader.endObject()
        return reactions
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: List<Reaction>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}