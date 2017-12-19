package chat.rocket.core.internal

import com.squareup.moshi.JsonAdapter
import se.ansman.kotshi.KotshiJsonAdapterFactory

@KotshiJsonAdapterFactory
abstract class CoreJsonAdapterFactory : JsonAdapter.Factory {
    companion object {
        val INSTANCE: CoreJsonAdapterFactory = KotshiCoreJsonAdapterFactory()
    }
}