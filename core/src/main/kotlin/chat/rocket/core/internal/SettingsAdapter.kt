package chat.rocket.core.internal

import chat.rocket.common.util.ifNull
import chat.rocket.core.model.Value
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class SettingsAdapter : JsonAdapter<Map<String, Value<Any>>>() {

    private val NAMES = arrayOf("_id", "type", "value")
    private val OPTIONS = JsonReader.Options.of(*NAMES)

    override fun toJson(writer: JsonWriter?, value: Map<String, Value<Any>>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @FromJson
    override fun fromJson(reader: JsonReader): Map<String, Value<Any>>? {
        reader.beginObject()
        assertNextName(reader, "settings")

        val map = HashMap<String, Value<Any>>()
        reader.beginArray()
        while (reader.hasNext()) {
            reader.beginObject()
            val (id, value) = readSetting(reader)
            reader.endObject()
            map[id] = value
        }

        return map
    }

    private fun readSetting(reader: JsonReader): Pair<String, Value<Any>> {
        var id: String? = null
        var type: String
        var tmp: Any? = null
        var token: JsonReader.Token
        while (reader.hasNext()) {
            when (reader.selectName(OPTIONS)) {
                0 -> {
                    id = reader.nextString()
                }
                1 -> {
                    type = reader.nextString()
                }
                2 -> {
                    token = reader.peek()
                    when (token) {
                        JsonReader.Token.NUMBER -> tmp = reader.nextInt()
                        JsonReader.Token.BOOLEAN -> tmp = reader.nextBoolean()
                        JsonReader.Token.STRING -> tmp = reader.nextString()
                        JsonReader.Token.BEGIN_OBJECT -> {
                            reader.beginObject()
                            assertNextName(reader, "defaultUrl")
                            tmp = reader.nextString()
                            reader.endObject()
                        }
                    }
                }
            }
        }

        id.ifNull {
            throw JsonEncodingException("Missing \"id\" field")
        }

        return when (tmp) {
            is String -> Pair(id!!, Value(tmp))
            is Int -> Pair(id!!, Value(tmp))
            is Boolean -> Pair(id!!, Value(tmp))
            else -> throw JsonEncodingException("Unknown value type for $tmp")
        }
    }

    private fun assertNextName(reader: JsonReader, expected: String) {
        var name = reader.nextName()
        if (name != expected) {
            throw JsonEncodingException("expected a \"$expected\" value, got \"$name\"")
        }
    }

}