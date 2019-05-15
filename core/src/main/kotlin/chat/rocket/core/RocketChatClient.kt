package chat.rocket.core

import chat.rocket.common.CommonJsonAdapterFactory
import chat.rocket.common.internal.FallbackSealedClassJsonAdapter
import chat.rocket.common.internal.ISO8601Date
import chat.rocket.common.model.TimestampAdapter
import chat.rocket.common.model.User
import chat.rocket.common.util.CalendarISO8601Converter
import chat.rocket.common.util.Logger
import chat.rocket.common.util.NoOpLogger
import chat.rocket.common.util.PlatformLogger
import chat.rocket.common.util.RealLogger
import chat.rocket.common.util.ifNull
import chat.rocket.core.internal.AttachmentAdapterFactory
import chat.rocket.core.internal.CoreJsonAdapterFactory
import chat.rocket.core.internal.MessageListAdapterFactory
import chat.rocket.core.internal.ReactionsAdapter
import chat.rocket.core.internal.RestMultiResult
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.RoomListAdapterFactory
import chat.rocket.core.internal.SettingsAdapter
import chat.rocket.core.internal.model.Subscription
import chat.rocket.core.internal.realtime.socket.Socket
import chat.rocket.core.internal.realtime.socket.model.State
import chat.rocket.core.internal.realtime.socket.model.StreamMessage
import chat.rocket.core.model.Message
import chat.rocket.core.model.Myself
import chat.rocket.core.model.Room
import chat.rocket.core.model.url.MetaJsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import java.security.InvalidParameterException
import kotlin.coroutines.CoroutineContext

class RocketChatClient private constructor(
    internal val httpClient: OkHttpClient,
    baseUrl: String,
    userAgent: String,
    internal val tokenRepository: TokenRepository,
    internal val logger: Logger
) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    internal val moshi: Moshi = Moshi.Builder()
        .add(FallbackSealedClassJsonAdapter.ADAPTER_FACTORY)
        .add(RestResult.JsonAdapterFactory())
        .add(RestMultiResult.JsonAdapterFactory())
        .add(SettingsAdapter())
        .add(AttachmentAdapterFactory(logger))
        .add(RoomListAdapterFactory(logger))
        .add(MessageListAdapterFactory(logger))
        .add(ReactionsAdapter())
        .add(MetaJsonAdapter.ADAPTER_FACTORY)
        .add(java.lang.Long::class.java, ISO8601Date::class.java, TimestampAdapter(CalendarISO8601Converter()))
        .add(Long::class.java, ISO8601Date::class.java, TimestampAdapter(CalendarISO8601Converter()))
        // XXX - MAKE SURE TO KEEP CommonJsonAdapterFactory and CoreJsonAdapterFactory as the latest Adapters...
        .add(CommonJsonAdapterFactory.INSTANCE)
        .add(CoreJsonAdapterFactory.INSTANCE)
        .build()

    internal lateinit var restUrl: HttpUrl
    val url: String
    val agent: String
    val roomsChannel = Channel<StreamMessage<Room>>(Channel.UNLIMITED)
    val subscriptionsChannel = Channel<StreamMessage<Subscription>>(Channel.UNLIMITED)
    val messagesChannel = Channel<Message>(Channel.UNLIMITED)
    val userDataChannel = Channel<Myself>(Channel.UNLIMITED)
    val activeUsersChannel = Channel<User>(Channel.UNLIMITED)
    val typingStatusChannel = Channel<Pair<String, Boolean>>(Channel.UNLIMITED)
    internal val socket: Socket

    init {
        url = sanitizeUrl(baseUrl)
        agent = userAgent

        HttpUrl.parse(url)?.let {
            restUrl = it
        }.ifNull {
            throw InvalidParameterException("You must pass a valid HTTP or HTTPS URL")
        }

        socket = Socket(
            client = this,
            roomsChannel = roomsChannel,
            subscriptionsChannel = subscriptionsChannel,
            messagesChannel = messagesChannel,
            userDataChannel = userDataChannel,
            activeUsersChannel = activeUsersChannel,
            typingStatusChannel = typingStatusChannel
        )
    }

    private fun sanitizeUrl(baseUrl: String): String {
        var url = baseUrl.trim()
        while (url.endsWith('/')) {
            url = url.dropLast(1)
        }
        return url
    }

    private constructor(builder: Builder) : this(
        builder.httpClient,
        builder.restUrl,
        builder.userAgent,
        builder.tokenRepository,
        if (builder.enableLogger) RealLogger(builder.platformLogger, builder.restUrl) else NoOpLogger)

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
        lateinit var userAgent: String
        lateinit var tokenRepository: TokenRepository
        lateinit var platformLogger: PlatformLogger
        var enableLogger: Boolean = true

        fun httpClient(init: Builder.() -> OkHttpClient) = apply { httpClient = init() }

        fun restUrl(init: Builder.() -> String) = apply { restUrl = init() }

        fun userAgent(init: Builder.() -> String) = apply { userAgent = init() }

        fun tokenRepository(init: Builder.() -> TokenRepository) = apply { tokenRepository = init() }

        fun platformLogger(init: Builder.() -> PlatformLogger) = apply { platformLogger = init() }

        fun enableLogger(init: Builder.() -> Boolean) = apply { enableLogger = init() }

        fun build() = RocketChatClient(this)
    }

    fun addStateChannel(channel: Channel<State>) {
        socket.statusChannelList.add(channel)
    }

    fun removeStateChannel(channel: Channel<State>) {
        socket.statusChannelList.remove(channel)
    }

    val state: State
        get() = socket.currentState
}
