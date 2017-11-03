package rocket.chat.kotlin.sample

import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenProvider
import chat.rocket.core.internal.rest.login
import chat.rocket.core.internal.rest.pinMessage
import chat.rocket.core.internal.rest.serverInfo
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
        restUrl = HttpUrl.parse("https://demo.rocket.chat/")!!
        websocketUrl = "wss://demo.rocket.chat/websocket"
        tokenProvider = MyTokenProvider()
        platformLogger = logger
    }

    client.login("username", "password", success = {
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
}

class MyTokenProvider : TokenProvider {
    var savedToken: Token? = null
    override fun save(token: Token) {
        savedToken = token
    }

    override fun get(): Token? {
        return savedToken
    }

}
