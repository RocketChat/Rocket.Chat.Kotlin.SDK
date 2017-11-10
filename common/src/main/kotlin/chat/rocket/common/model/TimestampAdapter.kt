package chat.rocket.common.model

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.util.ISO8601Converter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException
import java.text.ParseException

class TimestampAdapter(private val dateConverter: ISO8601Converter) : JsonAdapter<Long>() {
    private val options = JsonReader.Options.of("\$date")

    override fun toJson(writer: JsonWriter?, @ISO8601Date value: Long?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @ISO8601Date
    override fun fromJson(reader: JsonReader): Long? {
        var timestamp: Long? = null
        val result: String?
        val token = reader.peek()
        if (token == JsonReader.Token.BEGIN_OBJECT) {
            reader.beginObject()
            if (reader.hasNext()) {
                when (reader.selectName(options)) {
                    0 -> {
                        if (reader.peek() == JsonReader.Token.NULL) {
                            timestamp = reader.nextNull<Long>()
                        } else {
                            timestamp = reader.nextLong()
                        }
                    }
                }
            }
            reader.endObject()
        } else if (token == JsonReader.Token.STRING) {
            if (reader.peek() != JsonReader.Token.NULL) {
                result = reader.nextString()
                try {
                    timestamp = dateConverter.toTimestamp(result)
                } catch (e: ParseException) {
                    throw IOException("Error parsing date: " + result!!, e)
                }
            }
        }
        return timestamp
    }
}