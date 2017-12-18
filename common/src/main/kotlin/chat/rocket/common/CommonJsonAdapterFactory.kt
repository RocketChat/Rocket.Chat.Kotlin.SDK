package chat.rocket.common

import com.squareup.moshi.JsonAdapter
import se.ansman.kotshi.KotshiJsonAdapterFactory

@KotshiJsonAdapterFactory
abstract class CommonJsonAdapterFactory : JsonAdapter.Factory {
    companion object {
        val INSTANCE: CommonJsonAdapterFactory = KotshiCommonJsonAdapterFactory()
    }
}