package chat.rocket.core.internal.realtime

import chat.rocket.common.model.Token
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.model.MessageType
import chat.rocket.core.internal.model.SocketMessage
import chat.rocket.core.internal.model.Subscription
import chat.rocket.core.model.Room
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.experimental.Job
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

const val PING_INTERVAL = 10L

class Socket(internal val client: RocketChatClient,
             private val statusChannel: SendChannel<State>,
             internal val roomsChannel: SendChannel<StreamMessage<Room>>,
             internal val subscriptionsChannel: SendChannel<StreamMessage<Subscription>>
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
    internal var currentState: State = State.Disconnected
    internal var socket: WebSocket? = null
    internal var processingChannel: Channel<String>? = null
    internal var parentJob: Job? = null
    internal var readJob: Job? = null
    internal var pingJob: Job? = null
    internal val currentId = AtomicInteger(1)

    init {
        setState(State.Created)
        messageAdapter = moshi.adapter(SocketMessage::class.java)
    }

    internal fun connect() {
        // reset id counter
        currentId.set(1)
        parentJob = Job()
        processingChannel = Channel()
        setState(State.Connecting)
        socket = httpClient.newWebSocket(request, this)
    }

    internal fun disconnect() {
        socket?.close(1002, "Bye bye!!")
        setState(State.Disconnecting)
    }

    private fun processIncomingMessage(text: String) {
        logger.debug {
            "Incoming message: $text"
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
            State.Connecting -> {
                processConnectionMessage(message)
            }
            State.Authenticating -> {
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
                setState(State.Authenticating)
                login(client.tokenRepository.get())
            }
            else -> {
                logger.warn {
                    "Invalid message type on state Connecting: ${message.type}"
                }
            }
        }
    }

    private fun processAuthenticationResponse(message: SocketMessage, text: String) {
        when (message.type) {
            MessageType.ADDED -> {
            }
            MessageType.RESULT -> {
                processLoginResult(text)
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
            else -> {
                logger.debug {
                    "Ingnoring message type: ${message.type}"
                }
            }
        }
    }

    internal fun send(message: String) {
        logger.debug {
            "Sending message: $message"
        }
        socket?.send(message)
    }

    private fun reschedulePing(type: MessageType) {
        logger.debug {
            "Rescheduling ping in $PING_INTERVAL seconds"
        }

        pingJob?.cancel()
        pingJob = launch {
            delay(PING_INTERVAL, TimeUnit.SECONDS)
            socket?.send(pingMessage())
        }
    }

    internal fun setState(newState: State) {
        if (newState != currentState) {
            currentState = newState
            statusChannel.offer(currentState)
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
        webSocket.send(CONNECT_MESSAGE)
    }

    override fun onFailure(webSocket: WebSocket, throwable: Throwable?, response: Response?) {
        logger.warn {
            throwable?.message
        }
        throwable?.printStackTrace()
        setState(State.Disconnected)
        close()
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
    }

    override fun onMessage(webSocket: WebSocket, text: String?) {
        text?.let {
            processingChannel?.offer(it)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString?) {
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String?) {
        setState(State.Disconnected)
    }
}

fun Socket.login(token: Token?) {
    token?.let { authToken ->
        socket?.let {
            setState(State.Authenticating)
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
    object Created : State()
    object Connecting : State()
    object Authenticating : State()
    object Connected : State()
    object Disconnecting : State()
    object Disconnected : State()
}