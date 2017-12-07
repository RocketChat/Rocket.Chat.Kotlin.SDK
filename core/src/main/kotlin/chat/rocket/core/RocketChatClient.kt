package chat.rocket.core

import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.TimestampAdapter
import chat.rocket.common.util.CalendarISO8601Converter
import chat.rocket.common.util.Logger
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.internal.RestMultiResult
import chat.rocket.core.internal.RestResult
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.HttpUrl
import okhttp3.OkHttpClient

class RocketChatClient private constructor(var httpClient: OkHttpClient,
                                           var restUrl: HttpUrl,
                                           var websocketUrl: String,
                                           var tokenRepository: TokenRepository,
                                           var logger: Logger) {

    val moshi: Moshi = Moshi.Builder()
                        .add(RestResult.JsonAdapterFactory())
                        .add(RestMultiResult.JsonAdapterFactory())
                        .add(java.lang.Long::class.java, ISO8601Date::class.java, TimestampAdapter(CalendarISO8601Converter()))
                        .add(Long::class.java, ISO8601Date::class.java, TimestampAdapter(CalendarISO8601Converter()))
                        .add(KotlinJsonAdapterFactory())
                        .build()

    private constructor(builder: Builder) : this(builder.httpClient, builder.restUrl,
            builder.websocketUrl, builder.tokenRepository, Logger(builder.platformLogger))

    companion object {
        fun create(init: Builder.() -> Unit) = Builder(init).build()
    }

    class Builder private constructor() {
        constructor(init: Builder.() -> Unit) : this() {
            init()
        }

        lateinit var httpClient: OkHttpClient
        lateinit var restUrl: HttpUrl
        lateinit var websocketUrl: String
        lateinit var tokenRepository: TokenRepository
        lateinit var platformLogger: PlatformLogger

        fun httpClient(init: Builder.() -> OkHttpClient) = apply { httpClient = init() }

        fun restUrl(init: Builder.() -> HttpUrl) = apply { restUrl = init() }

        fun websocketUrl(init: Builder.() -> String) = apply { websocketUrl = init() }

        fun tokenRepository(init: Builder.() -> TokenRepository) = apply { tokenRepository = init() }

        fun platformLogger(init: Builder.() -> PlatformLogger) = apply { platformLogger = init() }

        fun build() = RocketChatClient(this)
    }
}