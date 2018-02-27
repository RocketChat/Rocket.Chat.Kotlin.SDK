package chat.rocket.core.model.url

import com.squareup.moshi.*
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Meta(
    val title: String?,
    val description: String?,
    val imageUrl: String?,
    val raw: Map<String, String>
)

class MetaJsonAdapter(moshi: Moshi) : JsonAdapter<Meta>() {
    private val type = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
    private val adapter: JsonAdapter<Map<String, String>> = moshi.adapter(type)

    override fun fromJson(reader: JsonReader): Meta? {
        val rawMeta = adapter.fromJson(reader)
        rawMeta?.let {
            val title = getTitle(rawMeta)
            val description = getDescription(rawMeta)
            val imageUrl = getImageUrl(rawMeta)

            return Meta(title, description, imageUrl, rawMeta)
        }

        return null
    }

    private fun getTitle(rawMeta: Map<String, String>): String? {
        rawMeta["ogTitle"]?.let { return it }
        rawMeta["twitterTitle"]?.let { return it }
        rawMeta["title"]?.let { return it }
        rawMeta["pageTitle"]?.let { return it }
        rawMeta["sailthruTitle"]?.let { return it }
        rawMeta["oembedTitle"]?.let { return it }
        return null
    }

    private fun getDescription(rawMeta: Map<String, String>): String? {
        rawMeta["ogDescription"]?.let { return it }
        rawMeta["twitterDescription"]?.let { return it }
        rawMeta["description"]?.let { return it }
        rawMeta["sailthruDescription"]?.let { return it }
        return null
    }

    private fun getImageUrl(rawMeta: Map<String, String>): String? {
        rawMeta["ogImage"]?.let { return it }
        rawMeta["twitterImageSrc"]?.let { return it }
        rawMeta["msapplicationTileImage"]?.let { return it }
        rawMeta["oembedThumbnailUrl"]?.let { return it }
        return null
    }

    override fun toJson(writer: JsonWriter, value: Meta?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val ADAPTER_FACTORY = JsonAdapter.Factory { type, annotations, moshi ->
            if (type == Meta::class.java) return@Factory MetaJsonAdapter(moshi)
            return@Factory null
        }
    }
}