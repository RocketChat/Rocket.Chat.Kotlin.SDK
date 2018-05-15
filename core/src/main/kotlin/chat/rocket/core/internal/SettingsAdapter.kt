package chat.rocket.core.internal

import chat.rocket.core.model.Value
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class SettingsAdapter : JsonAdapter<Map<String, Value<Any>>>() {

    private val NAMES = arrayOf("_id", "type", "value")
    private val OPTIONS = JsonReader.Options.of(*NAMES)

    override fun toJson(writer: JsonWriter, value: Map<String, Value<Any>>?) {
        writer.beginObject()
        writer.name("settings")
        writeSettingsObject(writer, value)
        writer.endObject()
    }

    private fun writeSettingsObject(writer: JsonWriter, value: Map<String, Value<Any>>?) {
        writer.beginArray()
        value?.let {
            for (entry in value.entries) {
                writer.beginObject()
                writer.name("_id")
                writer.value(entry.key)
                when (entry.value.value) {
                    is String -> {
                        writer.name("value")
                        writer.value(entry.value.value as String)
                        writer.name("type")
                        writer.value("string")
                    }
                    is Boolean -> {
                        writer.name("value")
                        writer.value(entry.value.value as Boolean)
                        writer.name("type")
                        writer.value("boolean")
                    }
                    is Int -> {
                        writer.name("value")
                        writer.value(entry.value.value as Int)
                        writer.name("type")
                        writer.value("int")
                    }
                }
                writer.endObject()
            }
        }

        writer.endArray()
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
        var type: String = ""
        var tmp: Any? = null
        var token: JsonReader.Token
        var nullToken = false
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
                        JsonReader.Token.NUMBER -> {
                            val number = reader.nextLongOrNull()
                            tmp = when {
                                number == null -> 0
                                number > Int.MAX_VALUE -> Int.MAX_VALUE
                                else -> number.toInt()
                            }
                        }
                        JsonReader.Token.BOOLEAN -> tmp = reader.nextBooleanOrFalse()
                        JsonReader.Token.STRING -> tmp = reader.nextStringOrNull() ?: ""
                        JsonReader.Token.BEGIN_OBJECT -> {
                            reader.beginObject()
                            tmp = readAsset(reader)
                            reader.endObject()
                        }
                        JsonReader.Token.NULL -> {
                            reader.skipValue()
                            nullToken = true
                        }
                    }
                }
            }
        }

        if (id == null) {
            throw JsonEncodingException("Missing \"id\" field")
        }

        // Since we can't guarantee the fields order, we need to set default values here, after parsing the whole object
        if (nullToken) {
            when (type) {
                "string" -> tmp = ""
                "boolean" -> tmp = false
                "int" -> tmp = 0
            }
        }

        return when (tmp) {
            is String -> Pair(id, Value(tmp))
            is Int -> Pair(id, Value(tmp))
            is Boolean -> Pair(id, Value(tmp))
            else -> throw JsonEncodingException("Unknown value type for $tmp")
        }
    }

    private val ASSET_NAMES = arrayOf("url", "defaultUrl")
    private val ASSET_OPTIONS = JsonReader.Options.of(*ASSET_NAMES)
    private fun readAsset(reader: JsonReader): String? {
        var url: String? = null
        var defaultUrl: String? = null
        while (reader.hasNext()) {
            when (reader.selectName(ASSET_OPTIONS)) {
                0 -> url = reader.nextString()
                1 -> defaultUrl = reader.nextString()
                else -> reader.skipValue()
            }
        }

        return url ?: defaultUrl
    }

    private fun assertNextName(reader: JsonReader, expected: String) {
        val name = reader.nextName()
        if (name != expected) {
            throw JsonEncodingException("expected a \"$expected\" value, got \"$name\"")
        }
    }
}