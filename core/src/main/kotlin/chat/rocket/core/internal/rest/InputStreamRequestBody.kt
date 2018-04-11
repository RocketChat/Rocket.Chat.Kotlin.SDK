package chat.rocket.core.internal.rest

import okio.Okio
import java.io.IOException
import okio.BufferedSink
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.InputStream

class InputStreamRequestBody(
    private val contentType: MediaType?,
    private val inputStreamProvider: () -> InputStream?
) : RequestBody() {

    override fun contentType(): MediaType? {
        return contentType
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return -1
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        inputStreamProvider().use { inputStream ->
            Okio.source(inputStream!!).use { source ->
                sink.writeAll(source)
            }
        }
    }
}