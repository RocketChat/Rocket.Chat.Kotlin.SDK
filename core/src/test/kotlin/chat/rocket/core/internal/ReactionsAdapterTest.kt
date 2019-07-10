package chat.rocket.core.internal

import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import chat.rocket.core.internal.rest.MESSAGE_WITHOUT_REACTION
import chat.rocket.core.internal.rest.MESSAGE_WITH_REACTION
import chat.rocket.core.model.Message
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.hamcrest.CoreMatchers.`is` as isEqualTo

class ReactionsAdapterTest {
    lateinit var moshi: Moshi
    @Mock private lateinit var tokenProvider: TokenRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        // Just to initialize Moshi
        val client = OkHttpClient()
        val rocket = RocketChatClient.create {
            httpClient = client
            restUrl = "http://8.8.8.8"
            userAgent = "Rocket.Chat.Kotlin.SDK"
            tokenRepository = tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        moshi = rocket.moshi
    }

    @Test
    fun `should deserialize JSON with reactions on message (without names)`() {
        val adapter = moshi.adapter<Message>(Message::class.java)
        adapter.fromJson(MESSAGE_WITH_REACTION)?.let { message ->
            assertThat(message.reactions?.size, isEqualTo(2))
        }
    }

    @Test
    fun `should deserialize JSON without reactions on message`() {
        val adapter = moshi.adapter<Message>(Message::class.java)
        adapter.fromJson(MESSAGE_WITHOUT_REACTION)?.let { message ->
            assertThat(message.id, isEqualTo("Xo9cGh9Cq6RTB9bw2"))
        }
    }
}