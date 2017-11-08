package rocket.chat.kotlin.sample

import chat.rocket.common.model.BaseRoom
import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenProvider
import chat.rocket.core.internal.rest.*
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

fun main(args:Array<String>) {
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
        pinMessage(client)
    }, error = {
        logger.debug(it.message!!)
    })

    client.serverInfo(success = {
        logger.debug("Server Version: ${it.version}")
    }, error = {
        logger.debug(it.message!!)
    })
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
    var savedToken: Token? = null
    override fun save(token: Token) {
        savedToken = token
    }

    override fun get(): Token? {
        return savedToken
    }

}
