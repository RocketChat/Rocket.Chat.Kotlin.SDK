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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

const val PING_INTERVAL = 15000L

class Socket(
    internal val client: RocketChatClient,
    internal val roomsChannel: SendChannel<StreamMessage<Room>>,
    internal val subscriptionsChannel: SendChannel<StreamMessage<Subscription>>,
    internal val messagesChannel: SendChannel<Message>,
    internal val userDataChannel: SendChannel<Myself>,
    internal val activeUsersChannel: SendChannel<User>,
    internal val typingStatusChannel: SendChannel<Pair<String, Boolean>>
) : WebSocketListener(), CoroutineScope {
    internal var parentJob = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

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
    private var readJob: Job? = null
    private var pingJob: Job? = null
    private var timeoutJob: Job? = null
    private val currentId = AtomicInteger(1)

    internal val subscriptionsMap = HashMap<String, (Boolean, String) -> Unit>()

    private val connectionContext = newSingleThreadContext("connection-context")
    private val reconnectionStrategy = ReconnectionStrategy()
    private var reconnectJob: Job? = null
    private var selfDisconnect = false

    private var messagesReceived = 0
    private var messagesProcessed = 0

    init {
        setState(State.Created())
        messageAdapter = moshi.adapter(SocketMessage::class.java)
    }

    internal fun connect(resetCounter: Boolean = false) {
        selfDisconnect = false
        // reset id counter
        currentId.set(1)
        parentJob.cancel()
        reconnectJob?.cancel()

        if (resetCounter) {
            reconnectionStrategy.reset()
        }

        parentJob = Job()
        processingChannel = Channel(Channel.UNLIMITED)
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
            logger.info { "SELF DISCONNECTED, WON'T TRY TO RECONNECT." }
            return
        }

        logger.info { "START RECONNECTION" }

        if (reconnectionStrategy.shouldRetry) {
            reconnectJob?.cancel()
            reconnectJob = launch(connectionContext) {
                logger.debug { "RECONNECTING IN: ${reconnectionStrategy.reconnectInterval}" }
                delayReconnection(reconnectionStrategy.reconnectInterval)
                if (!isActive) {
                    logger.debug { "RECONNECT JOB INACTIVE, IGNORING" }
                    return@launch
                }
                reconnectionStrategy.processAttempts()
                connect(false)
            }
        } else {
            logger.info { "EXHAUSTED RECONNECTION ATTEMPTS: ${reconnectionStrategy.numberOfAttempts} - ${reconnectionStrategy.maxAttempts}" }
        }
    }

    private suspend fun delayReconnection(reconnectInterval: Int) {
        val seconds = reconnectInterval / 1000
        withContext(connectionContext) {
            for (second in 0..(seconds - 1)) {
                if (!coroutineContext.isActive) {
                    logger.debug { "Reconnect job inactive, ignoring" }
                    return@withContext
                }
                val left = seconds - second
                logger.debug { "$left second(s) left" }
                setState(State.Waiting(left))
                delay(1000)
            }
        }
    }

    private fun processIncomingMessage(text: String) {
        messagesProcessed++
        logger.debug {
            val len = Math.min(40, text.length)
            "PROCESS INCOMING MESSAGE: ${text.substring(0, len)}"
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

                // Also process the message
                if (message.type == MessageType.ADDED) {
                    processSubscriptionsAdded(message, text)
                }
            }
            MessageType.RESULT -> processLoginResult(text)
            MessageType.PING -> send(pongMessage())
            else -> {
                // IGNORING FOR NOW.
            }
        }
    }

    private fun processMessage(message: SocketMessage, text: String) {
        when (message.type) {
            MessageType.PING -> {
                logger.debug { "Sending pong - messages received $messagesReceived - messages processed $messagesProcessed" }
                send(pongMessage())
            }
            MessageType.ADDED -> processSubscriptionsAdded(message, text)
            MessageType.REMOVED -> processSubscriptionsRemoved(message, text)
            MessageType.CHANGED -> processSubscriptionsChanged(message, text)
            MessageType.READY -> processSubscriptionResult(text)
            MessageType.RESULT -> processMethodResult(text)
            MessageType.ERROR -> logger.warn { "ERROR on processMessage: ${message.errorReason}" }
            else -> logger.debug { "Ignoring message type: ${message.type}" }
        }
    }

    internal fun send(message: String) {
        logger.debug { "Sending messagE: $message" }
        socket?.send(message)
    }

    private fun reschedulePing(type: MessageType) {
        logger.debug { "Rescheduling ping in $PING_INTERVAL milliseconds" }

        timeoutJob?.cancel()
        pingJob?.cancel()

        pingJob = launch {
            logger.debug { "Scheduling ping" }
            delay(PING_INTERVAL)

            logger.debug { "Running ping if active" }
            if (!isActive) return@launch
            schedulePingTimeout()
            logger.debug { "Sending ping - messages received $messagesReceived - messages processed $messagesProcessed" }
            send(pingMessage())
        }
    }

    private suspend fun schedulePingTimeout() {
        val timeout = (PING_INTERVAL * 1.5).toLong()
        logger.debug { "Scheduling ping timeout in $timeout milliseconds" }
        timeoutJob = launch(parentJob) {
            delay(timeout)
            if (!isActive) return@launch
            when (currentState) {
                is State.Disconnected,
                is State.Disconnecting -> logger.warn { "Pong not received, but already disconnected" }
                else -> {
                    logger.warn { "Pong not received" }
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
        launch(connectionContext) {
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
        parentJob.cancel()
    }

    override fun onOpen(webSocket: WebSocket, response: Response?) {
        readJob = launch {
            for (message in processingChannel!!) {
                processIncomingMessage(message)
            }
        }
        reconnectionStrategy.reset()
        send(CONNECT_MESSAGE)
    }

    override fun onFailure(webSocket: WebSocket, throwable: Throwable?, response: Response?) {
        logger.warn { "Socket.onFailure(). THROWABLE MESSAGE: ${throwable?.message} -  RESPONSE MESSAGE: ${response?.message()}" }
        throwable?.printStackTrace()
        setState(State.Disconnected())
        close()
        startReconnection()
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
        logger.warn { "Socket.onClosing() called. Received CODE = $code - Received REASON = $reason" }
        setState(State.Disconnecting())
        startReconnection()
    }
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String?) {
        logger.warn { "Socket.onClosed() called. Received CODE = $code - Received REASON = $reason" }
        setState(State.Disconnected())
        close()
        startReconnection()
    }

    override fun onMessage(webSocket: WebSocket, text: String?) {
        logger.warn { "Socket.onMessage(). Received TEXT = $text for processing channel = $processingChannel" }
        text?.let {
            messagesReceived++
            if (!parentJob.isActive) {
                logger.debug { "Parent job: $parentJob" }
            }
            launch {
                if (processingChannel == null || processingChannel?.isFull == true || processingChannel?.isClosedForSend == true) {
                    logger.debug { "processing channel is in trouble... $processingChannel - full ${processingChannel?.isFull} - closedForSend ${processingChannel?.isClosedForSend}" }
                }
                processingChannel?.send(it)
            }
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString?) {
        logger.warn { "Socket.onMessage() called. Received ByteString message: $bytes" }
    }
}

fun RocketChatClient.connect(resetCounter: Boolean = false) = socket.connect(resetCounter)

fun RocketChatClient.disconnect() = socket.disconnect()
