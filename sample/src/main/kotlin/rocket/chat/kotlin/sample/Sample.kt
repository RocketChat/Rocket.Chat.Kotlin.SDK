package rocket.chat.kotlin.sample

import chat.rocket.common.RocketChatException
import chat.rocket.common.model.BaseRoom
import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenProvider
import chat.rocket.core.internal.rest.channelSubscriptions
import chat.rocket.core.internal.rest.coroutines.me
import chat.rocket.core.internal.rest.getRoomFavoriteMessages
import chat.rocket.core.internal.rest.login
import chat.rocket.core.internal.rest.pinMessage
import chat.rocket.core.internal.rest.serverInfo
import chat.rocket.core.model.Myself
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

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
            .addInterceptor(interceptor).build()

    val client = RocketChatClient.create {
        httpClient = okHttpClient
        restUrl = HttpUrl.parse("http://localhost:3000/")!!
        websocketUrl = "ws://localhost:3000/websocket"
        tokenProvider = SimpleTokenProvider()
        platformLogger = logger
    }

    client.login("testuser", "testpass", success = {
        logger.debug("Login: ${it.userId} - ${it.authToken}")
        //pinMessage(client)
        /*client.me(success = {
            logger.debug("User: $it")
        }, error = {
            it.printStackTrace()
        })*/
        launch(CommonPool) {
            try {
                val myself = client.me()
                logger.debug(myself.toString())
            } catch (ex: RocketChatException) {
                ex.printStackTrace()
            }
        }

        getSubscriptions(client)
    }, error = {
        it.printStackTrace()
        logger.debug(it.message!!)
    })

    client.serverInfo(success = {
        logger.debug("Server Version: ${it.version}")
    }, error = {
        logger.debug(it.message!!)
    })
}

fun getSubscriptions(client: RocketChatClient) {
    /*client.channelSubscriptions(success = { rooms: List<BaseRoom>, total: Long ->
        println("Channels: $rooms, total: $total")
    }, error = {
        it.printStackTrace()
    })*/

    /*client.groupSubscriptions(success = { rooms: List<Room>, total: Long ->
        println("Groups: $rooms, total: $total")
    }, error = {
        it.printStackTrace()
    })

    client.dmSubscriptions(success = { rooms: List<Room>, total: Long ->
        println("DM: $rooms, total: $total")
    }, error = {
        it.printStackTrace()
    })*/
}

fun pinMessage(client: RocketChatClient) {
    client.pinMessage("messageId", success = {
        println(it)
    }, error = {
        println(it.message!!)
    })

    client.getRoomFavoriteMessages("GENERAL", BaseRoom.RoomType.PUBLIC, 0, success = {
        messages, _ -> for (message in messages) println(message)
    }, error = {
        println(it.message!!)
    })
}

class SimpleTokenProvider : TokenProvider {
    private var savedToken: Token? = null
    override fun save(token: Token) {
        savedToken = token
    }

    override fun get(): Token? {
        return savedToken
    }
}
