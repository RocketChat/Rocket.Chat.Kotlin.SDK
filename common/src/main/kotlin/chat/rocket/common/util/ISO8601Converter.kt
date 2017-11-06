package chat.rocket.common.util

import java.text.ParseException

interface ISO8601Converter {
    fun fromTimestamp(timestamp: Long): String

    @Throws(ParseException::class)
    fun toTimestamp(date: String): Long
}