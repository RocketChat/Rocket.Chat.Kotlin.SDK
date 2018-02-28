package chat.rocket.core.internal.realtime

import chat.rocket.common.model.Token
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.model.MessageType
import chat.rocket.core.internal.model.SocketMessage
import chat.rocket.core.internal.model.Subscription
import chat.rocket.core.model.Message
import chat.rocket.core.model.Room
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

const val PING_INTERVAL = 15L

class Socket(internal val client: RocketChatClient,
             private val statusChannel: SendChannel<State>,
             internal val roomsChannel: SendChannel<StreamMessage<Room>>,
             internal val subscriptionsChannel: SendChannel<StreamMessage<Subscription>>,
             internal val messagesChannel: SendChannel<Message>
) : WebSocketListener() {

    private val request: Request = Request.Builder()
                .url("${client.url}/websocket")
                .addHeader("Accept-Encoding", "gzip, deflate, sdch")
                .addHeader("Accept-Language", "en-US,en;q=0.8")
                .addHeader("Pragma", "no-cache")
                .addHeader("User-Agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36")
                .build()

    private val httpClient = client.httpClient
    internal val logger = client.logger
    internal val moshi = client.moshi
    internal val messageAdapter: JsonAdapter<SocketMessage>
    internal var currentState: State = State.Disconnected()
    internal var socket: WebSocket? = null
    internal var processingChannel: Channel<String>? = null
    internal var parentJob: Job? = null
    internal var readJob: Job? = null
    internal var pingJob: Job? = null
    private var reconnectJob: Job? = null
    private var timeoutJob: Job? = null
    internal val currentId = AtomicInteger(1)

    internal val subscriptionsMap = HashMap<String, (Boolean, String) -> Unit>()

    private val reconnectionStrategy = ReconnectionStrategy(Int.MAX_VALUE, 3000)

    private var selfDisconnect = false

    init {
        setState(State.Created())
        messageAdapter = moshi.adapter(SocketMessage::class.java)
    }

    internal fun connect() {
        selfDisconnect = false
        // reset id counter
        currentId.set(1)
        parentJob?.cancel()
        reconnectJob?.cancel()

        parentJob = Job()
        processingChannel = Channel()
        setState(State.Connecting())
        socket = httpClient.newWebSocket(request, this)
    }

    internal fun disconnect() {
        when (currentState) {
            State.Disconnected() -> return
            else -> {
                selfDisconnect = true
                socket?.close(1002, "Bye bye!!")
                pingJob?.cancel()
                timeoutJob?.cancel()
                reconnectJob?.cancel()
                setState(State.Disconnecting())
            }
        }
    }

    private fun startReconnection() {
        // Ignore  self disconnection
        if (selfDisconnect) return

        if (!selfDisconnect) {
            if (reconnectionStrategy.numberOfAttempts < reconnectionStrategy.maxAttempts) {
                reconnectJob?.cancel()
                reconnectJob = launch {
                    logger.debug {
                        "SDK: Reconnecting in: ${reconnectionStrategy.reconnectInterval}"
                    }
                    delayReconnection(reconnectionStrategy.reconnectInterval)
                    if (!isActive) return@launch
                    reconnectionStrategy.processAttempts()
                    connect()
                }
            }
        }
    }

    private suspend fun delayReconnection(reconnectInterval: Int) {
        val seconds = reconnectInterval / 1000
        async {
            for (second in 1..seconds) {
                if (!isActive) return@async
                val left = seconds - second
                logger.debug { "SDK: $left second(s) left" }
                setState(State.Waiting(left))
                delay(1000)
            }
        }.await()
    }

    private fun processIncomingMessage(text: String) {
        logger.debug {
            "SDK: Incoming message: $text"
        }

        // Ignore empty or invalid messages
        val message: SocketMessage
        try {
            message = messageAdapter.fromJson(text) ?: return
        } catch (ex: Exception) {
            ex.printStackTrace()
            return
        }

        reschedulePing(message.type)

        when (currentState) {
            is State.Connecting -> {
                processConnectionMessage(message)
            }
            is State.Authenticating -> {
                processAuthenticationResponse(message, text)
            }
            else -> {
                processMessage(message, text)
            }
        }
    }

    private fun processConnectionMessage(message: SocketMessage) {
        when (message.type) {
            MessageType.CONNECTED -> {
                setState(State.Authenticating())
                login(client.tokenRepository.get())
            }
            else -> {
                logger.warn {
                    "SDK: Invalid message type on state Connecting: ${message.type}"
                }
            }
        }
    }

    private fun processAuthenticationResponse(message: SocketMessage, text: String) {
        when (message.type) {
            MessageType.ADDED, MessageType.UPDATED -> {
                // FIXME - for now just set the state to connected
                setState(State.Connected())
            }
            MessageType.RESULT -> {
                processLoginResult(text)
            }
            MessageType.PING -> {
                send(pongMessage())
            }
            else -> {
            }
        }
    }

    private fun processMessage(message: SocketMessage, text: String) {
        when (message.type) {
            MessageType.PING -> {
                send(pongMessage())
            }
            MessageType.CHANGED -> {
                processSubscriptionsChanged(message, text)
            }
            MessageType.READY -> {
                processSubscriptionResult(text)
            }
            else -> {
                logger.debug {
                    "SDK: Ingnoring message type: ${message.type}"
                }
            }
        }
    }

    internal fun send(message: String) {
        logger.debug {
            "SDK: Sending message: $message"
        }
        socket?.send(message)
    }

    private fun reschedulePing(type: MessageType) {
        logger.debug {
            "SDK: Rescheduling ping in $PING_INTERVAL seconds"
        }

        timeoutJob?.cancel()

        pingJob?.cancel()
        pingJob = launch(parent = parentJob) {
            logger.debug { "SDK: Scheduling ping" }
            delay(PING_INTERVAL, TimeUnit.SECONDS)

            logger.debug { "SDK: running ping if active" }
            if (!isActive) return@launch
            schedulePingTimeout()
            logger.debug { "SDK: sending ping" }
            send(pingMessage())
        }
    }

    private suspend fun schedulePingTimeout() {
        val timeout = (PING_INTERVAL * 1.5).toLong()
        logger.debug { "SDK: Scheduling ping timeout in $timeout" }
        timeoutJob = launch(parent = parentJob) {
            delay(timeout, TimeUnit.SECONDS)

            if (!isActive) return@launch
            when (currentState) {
                is State.Disconnected,
                is State.Disconnecting-> {
                    logger.warn { "SDK: PONG not received, but already disconnected" }
                }
                else -> {
                    logger.warn { "SDK: PONG not received" }
                    socket?.cancel()
                }
            }
        }
    }

    internal fun setState(newState: State) {
        if (newState != currentState) {
            currentState = newState
            launch() {
                statusChannel.send(currentState)
            }
        }
    }

    internal fun generateId(): String {
        return currentId.getAndIncrement().toString()
    }

    private fun close() {
        processingChannel?.close()
        parentJob?.cancel()
    }

    override fun onOpen(webSocket: WebSocket, response: Response?) {
        readJob = launch(parent = parentJob) {
            for (message in processingChannel!!) {
                processIncomingMessage(message)
            }
        }
        reconnectionStrategy.numberOfAttempts = 0
        send(CONNECT_MESSAGE)
    }

    override fun onFailure(webSocket: WebSocket, throwable: Throwable?, response: Response?) {
        logger.warn {
            throwable?.message
        }
        throwable?.printStackTrace()
        setState(State.Disconnected())
        close()
        startReconnection()
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
        setState(State.Disconnecting())
    }

    override fun onMessage(webSocket: WebSocket, text: String?) {
        logger.debug { "SDK: Received text message: $text, channel: $processingChannel" }
        text?.let {
            launch(parent = parentJob) { processingChannel?.send(it) }
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString?) {
        logger.debug { "SDK: Received ByteString message: ${bytes.toString()}" }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String?) {
        setState(State.Disconnected())
        close()
        startReconnection()
    }
}

fun Socket.login(token: Token?) {
    token?.let { authToken ->
        socket?.let {
            setState(State.Authenticating())
            send(loginMethod(generateId(), authToken.authToken))
        }
    }
}

fun RocketChatClient.connect() {
    socket.connect()
}

fun RocketChatClient.disconnect() {
    socket.disconnect()
}

sealed class State {
    class Created : State()
    class Waiting(val seconds: Int) : State()
    class Connecting : State()
    class Authenticating : State()
    class Connected : State()
    class Disconnecting : State()
    class Disconnected : State()
}