package rocket.chat.kotlin.sample

import chat.rocket.common.RocketChatException
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.ServerInfo
import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import chat.rocket.core.compat.Callback
import chat.rocket.core.compat.serverInfo
import chat.rocket.core.internal.realtime.*
import chat.rocket.core.internal.realtime.socket.model.State
import chat.rocket.core.internal.realtime.socket.connect
import chat.rocket.core.internal.rest.*
import chat.rocket.core.model.history
import chat.rocket.core.model.messages
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {

    val logger = object : PlatformLogger {
        override fun debug(s: String) {
            println(s)
        }

        override fun info(s: String) {
            println(s)
        }

        override fun warn(s: String) {
            println(s)
        }
    }

    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val client = RocketChatClient.create {
        httpClient = okHttpClient
        restUrl = "https://your-server.rocket.chat"
        userAgent = "Rocket.Chat.Kotlin.SDK"
        tokenRepository = SimpleTokenRepository()
        platformLogger = logger
    }

    val job = GlobalScope.launch(Dispatchers.IO) {
        val token = client.login("your-username", "your-password")
        logger.debug("Token: userId = ${token.userId} - authToken = ${token.authToken}")

        launch {
            val statusChannel = Channel<State>()
            client.addStateChannel(statusChannel)
            for (status in statusChannel) {
                logger.debug("CHANGING STATUS TO: $status")
                if (status is State.Connected) {
                        logger.debug("Connected!")
                        client.subscribeSubscriptions { _, _ -> }
                        client.subscribeRooms { _, _ -> }
                        client.subscribeUserData { _, _ -> }
                        client.subscribeActiveUsers { _, _ ->  }
                        client.subscribeTypingStatus("GENERAL") {_, _ ->  }
                }
            }
        }

        launch {
            for (room in client.roomsChannel) {
                logger.debug("Room: $room")
            }
        }

        launch {
            for (subscription in client.subscriptionsChannel) {
                logger.debug("Subscription: $subscription")
            }
        }

        launch {
            for (userData in client.userDataChannel) {
                logger.debug("User Data: $userData")
            }
        }

        launch {
            for (activeUsers in client.activeUsersChannel) {
                logger.debug("Active users: $activeUsers")
            }
        }

        launch {
            for (typingStatus in client.typingStatusChannel) {
                logger.debug("Typing status: $typingStatus")
            }
        }

        launch {
//            delay(10000)
//            client.setTemporaryStatus(UserStatus.Online())
//            delay(2000)
//            client.setDefaultStatus(UserStatus.Away())
//            client.setTypingStatus("GENERAL", "testing", true)
            showFileList(client)
        }

        client.connect()
        logger.debug("PERMISSIONS: ${client.permissions()}")

/*        client.sendMessage(roomId = "GENERAL",
                text = "Sending message from SDK to #general and @here with url https://github.com/RocketChat/Rocket.Chat.Kotlin.SDK/",
                alias = "TestingAlias",
                emoji = ":smirk:",
                avatar = "https://avatars2.githubusercontent.com/u/224255?s=88&v=4")
*/

        val rooms = client.chatRooms()
        logger.debug("CHAT ROOMS: $rooms")
        val room = rooms.update.lastOrNull { room -> room.id.contentEquals("GENERAL") }
        logger.debug("ROOM: $room")
        logger.debug("MESSAGES: ${room?.messages()}")
        logger.debug("HISTORY: ${room?.history()}")
    }

    // simple old callbacks
    client.serverInfo(object : Callback<ServerInfo> {
        override fun onSuccess(data: ServerInfo) {
            logger.debug("SERVER INFO: $data")
        }

        override fun onError(error: RocketChatException) {
            error.printStackTrace()
        }
    })

    runBlocking {
        //delay(10000)
        //client.disconnect()
        job.join()
        //delay(2000)
        //  exitProcess(-1)
    }
}

suspend fun showFavoriteMessage(client: RocketChatClient) {
        val result = client.getFavoriteMessages("GENERAL", RoomType.Channel(), 0)
        println("favoriteMessages: $result")
}

suspend fun showFileList(client: RocketChatClient) {
        val result = client.getFiles("GENERAL", RoomType.Channel(), 0)
        println("Attachment from the File List: $result")
}

class SimpleTokenRepository : TokenRepository {
    private var savedToken: Token? = null
    override fun save(url: String, token: Token) {
        savedToken = token
    }

    override fun get(url: String): Token? {
        return savedToken
    }
}
