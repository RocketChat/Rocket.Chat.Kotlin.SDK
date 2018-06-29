package chat.rocket.core.internal.realtime.socket

import chat.rocket.common.model.User
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.model.Subscription
import chat.rocket.core.internal.realtime.message.CONNECT_MESSAGE
import chat.rocket.core.internal.realtime.message.pingMessage
import chat.rocket.core.internal.realtime.message.pongMessage
import chat.rocket.core.internal.realtime.socket.message.model.MessageType
import chat.rocket.core.internal.realtime.socket.message.model.SocketMessage
import chat.rocket.core.internal.realtime.socket.model.ReconnectionStrategy
import chat.rocket.core.internal.realtime.socket.model.State
import chat.rocket.core.internal.realtime.socket.model.StreamMessage
import chat.rocket.core.model.Message
import chat.rocket.core.model.Myself
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

class Socket(
    internal val client: RocketChatClient,
    internal val roomsChannel: SendChannel<StreamMessage<Room>>,
    internal val subscriptionsChannel: SendChannel<StreamMessage<Subscription>>,
    internal val messagesChannel: SendChannel<Message>,
    internal val userDataChannel: SendChannel<Myself>,
    internal val activeUsersChannel: SendChannel<User>,
    internal val typingStatusChannel: SendChannel<Pair<String, Boolean>>
) : WebSocketListener() {

    private val request: Request = Request.Builder()
        .url("${client.url}/websocket")
        .addHeader("Accept-Encoding", "gzip, deflate, sdch")
        .addHeader("Accept-Language", "en-US,en;q=0.8")
        .addHeader("Pragma", "no-cache")
        .header("User-Agent", client.agent)
        .build()

    private val httpClient = client.httpClient
    internal val logger = client.logger
    internal val moshi = client.moshi
    private val messageAdapter: JsonAdapter<SocketMessage>
    internal var currentState: State = State.Disconnected()
    internal var socket: WebSocket? = null
    private var processingChannel: Channel<String>? = null
    internal val statusChannelList = ArrayList<Channel<State>>()
    internal var parentJob: Job? = null
    private var readJob: Job? = null
    private var pingJob: Job? = null
    private var reconnectJob: Job? = null
    private var timeoutJob: Job? = null
    private val currentId = AtomicInteger(1)

    internal val subscriptionsMap = HashMap<String, (Boolean, String) -> Unit>()

    private val reconnectionStrategy =
        ReconnectionStrategy(Int.MAX_VALUE, 3000)

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
                logger.debug { "SELF DISCONNECT" }
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
        if (selfDisconnect) {
            logger.info { "Self disconnected, won't try to reconnect." }
            return
        }

        logger.info { "startReconnection" }

        if (reconnectionStrategy.numberOfAttempts < reconnectionStrategy.maxAttempts) {
            reconnectJob?.cancel()
            reconnectJob = launch {
                logger.debug {
                    "Reconnecting in: ${reconnectionStrategy.reconnectInterval}"
                }
                delayReconnection(reconnectionStrategy.reconnectInterval)
                if (!isActive) return@launch
                reconnectionStrategy.processAttempts()
                connect()
            }
        } else {
            logger.info { "Exhausted reconnection attempts: ${reconnectionStrategy.numberOfAttempts} - ${reconnectionStrategy.maxAttempts}" }
        }
    }

    private suspend fun delayReconnection(reconnectInterval: Int) {
        val seconds = reconnectInterval / 1000
        async {
            for (second in 0..(seconds - 1)) {
                if (!isActive) return@async
                val left = seconds - second
                logger.debug { "$left second(s) left" }
                setState(State.Waiting(left))
                delay(1000)
            }
        }.await()
    }

    private fun processIncomingMessage(text: String) {
        logger.debug {
            val len = Math.min(40, text.length)
            "Process Incoming message: ${text.substring(0, len)}"
        }

        // Ignore empty or invalid messages
        val message: SocketMessage
        try {
            message = messageAdapter.fromJson(text) ?: return
        } catch (ex: Exception) {
            logger.debug { "Error parsing message, ignoring it" }
            ex.printStackTrace()
            return
        }

        reschedulePing(message.type)

        when (currentState) {
            is State.Connecting -> {
                logger.debug { "State machine: CONNECTING" }
                processConnectionMessage(message)
            }
            is State.Authenticating -> {
                logger.debug { "State machine: AUTHENTICATING" }
                processAuthenticationResponse(message, text)
            }
            else -> {
                logger.debug { "State machine: CONNECTED" }
                processMessage(message, text)
            }
        }
    }

    private fun processConnectionMessage(message: SocketMessage) {
        when (message.type) {
            MessageType.CONNECTED -> {
                setState(State.Authenticating())
                login(client.tokenRepository.get(client.url))
            }
            else -> {
                logger.warn { "Invalid message type on state Connecting: ${message.type}" }
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
                // IGNORING FOR NOW.
            }
        }
    }

    private fun processMessage(message: SocketMessage, text: String) {
        when (message.type) {
            MessageType.PING -> {
                send(pongMessage())
            }
            MessageType.ADDED -> {
                processSubscriptionsAdded(message, text)
            }
            MessageType.REMOVED -> {
                processSubscriptionsRemoved(message, text)
            }
            MessageType.CHANGED -> {
                processSubscriptionsChanged(message, text)
            }
            MessageType.READY -> {
                processSubscriptionResult(text)
            }
            MessageType.ERROR -> {
                logger.info { "Error: ${message.errorReason}" }
            }
            else -> {
                logger.debug { "Ignoring message type: ${message.type}" }
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

        timeoutJob?.cancel()

        pingJob?.cancel()
        pingJob = launch(parent = parentJob) {
            logger.debug { "Scheduling ping" }
            delay(PING_INTERVAL, TimeUnit.SECONDS)

            logger.debug { "running ping if active" }
            if (!isActive) return@launch
            schedulePingTimeout()
            logger.debug { "sending ping" }
            send(pingMessage())
        }
    }

    private suspend fun schedulePingTimeout() {
        val timeout = (PING_INTERVAL * 1.5).toLong()
        logger.debug { "Scheduling ping timeout in $timeout" }
        timeoutJob = launch(parent = parentJob) {
            delay(timeout, TimeUnit.SECONDS)

            if (!isActive) return@launch
            when (currentState) {
                is State.Disconnected,
                is State.Disconnecting -> {
                    logger.warn { "PONG not received, but already disconnected" }
                }
                else -> {
                    logger.warn { "PONG not received" }
                    socket?.cancel()
                }
            }
        }
    }

    internal fun setState(newState: State) {
        if (newState != currentState) {
            logger.debug { "Setting state to: $newState - oldState: $currentState, channels: ${statusChannelList.size}" }
            currentState = newState
            sendState(newState)
        }
    }

    private fun sendState(state: State) {
        launch {
            for (channel in statusChannelList) {
                logger.debug { "Sending $state to $channel" }
                channel.send(state)
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
        logger.warn { throwable?.message }
        throwable?.printStackTrace()
        setState(State.Disconnected())
        close()
        startReconnection()
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
        logger.debug { "webSocket.onClosing - CLOSING SOCKET" }
        setState(State.Disconnecting())
        startReconnection()
    }

    override fun onMessage(webSocket: WebSocket, text: String?) {
        logger.debug { "Received text message: $text, channel: $processingChannel" }
        text?.let {
            launch(parent = parentJob) { processingChannel?.send(it) }
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString?) {
        logger.debug { "Received ByteString message: $bytes" }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String?) {
        setState(State.Disconnected())
        close()
        startReconnection()
    }
}

fun RocketChatClient.connect() {
    socket.connect()
}

fun RocketChatClient.disconnect() {
    socket.disconnect()
}