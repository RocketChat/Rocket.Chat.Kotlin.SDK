package chat.rocket.core

import chat.rocket.common.CommonJsonAdapterFactory
import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.TimestampAdapter
import chat.rocket.common.util.CalendarISO8601Converter
import chat.rocket.common.util.Logger
import chat.rocket.common.util.PlatformLogger
import chat.rocket.common.util.ifNull
import chat.rocket.core.internal.CoreJsonAdapterFactory
import chat.rocket.core.internal.RestMultiResult
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.SettingsAdapter
import chat.rocket.core.internal.model.Subscription
import chat.rocket.core.internal.realtime.State
import chat.rocket.core.internal.realtime.Socket
import chat.rocket.core.internal.realtime.StreamMessage
import chat.rocket.core.model.Room
import com.squareup.moshi.Moshi
import kotlinx.coroutines.experimental.channels.Channel
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import java.security.InvalidParameterException

class RocketChatClient private constructor(internal val httpClient: OkHttpClient,
                                           internal val baseUrl: String,
                                           internal val tokenRepository: TokenRepository,
                                           internal val logger: Logger) {

    internal val moshi: Moshi = Moshi.Builder()
            .add(RestResult.JsonAdapterFactory())
            .add(RestMultiResult.JsonAdapterFactory())
            .add(SettingsAdapter())
            .add(java.lang.Long::class.java, ISO8601Date::class.java, TimestampAdapter(CalendarISO8601Converter()))
            .add(Long::class.java, ISO8601Date::class.java, TimestampAdapter(CalendarISO8601Converter()))
            .add(CommonJsonAdapterFactory.INSTANCE)
            .add(CoreJsonAdapterFactory.INSTANCE)
            .build()

    internal lateinit var restUrl: HttpUrl
    internal val url: String
    val statusChannel = Channel<State>()
    val roomsChannel = Channel<StreamMessage<Room>>()
    val subscriptionsChannel = Channel<StreamMessage<Subscription>>()
    internal val socket: Socket

    init {
        url = sanatizeUrl(baseUrl)
        HttpUrl.parse(url)?.let {
            restUrl = it
        }.ifNull {
            throw InvalidParameterException("You must pass a valid HTTP or HTTPS URL")
        }
        socket = Socket(this, statusChannel, roomsChannel, subscriptionsChannel)
    }

    private fun sanatizeUrl(baseUrl: String): String {
        var url = baseUrl.trim()
        while (url.endsWith('/')) {
            url = url.dropLast(1)
        }

        return url
    }

    private constructor(builder: Builder) : this(builder.httpClient, builder.restUrl,
            builder.tokenRepository, Logger(builder.platformLogger))

    companion object {
        val CONTENT_TYPE_JSON = MediaType.parse("application/json; charset=utf-8")

        fun create(init: Builder.() -> Unit) = Builder(init).build()
    }

    class Builder private constructor() {
        constructor(init: Builder.() -> Unit) : this() {
            init()
        }

        lateinit var httpClient: OkHttpClient
        lateinit var restUrl: String
        lateinit var tokenRepository: TokenRepository
        lateinit var platformLogger: PlatformLogger

        fun httpClient(init: Builder.() -> OkHttpClient) = apply { httpClient = init() }

        fun restUrl(init: Builder.() -> String) = apply { restUrl = init() }

        fun tokenRepository(init: Builder.() -> TokenRepository) = apply { tokenRepository = init() }

        fun platformLogger(init: Builder.() -> PlatformLogger) = apply { platformLogger = init() }

        fun build() = RocketChatClient(this)
    }

    val state: State
        get() = socket.currentState
}