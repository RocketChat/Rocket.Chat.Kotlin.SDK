package chat.rocket.core.internal.rest

import chat.rocket.common.model.RoomType
import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import io.fabric8.mockwebserver.DefaultMockServer
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.OkHttpClient
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.hamcrest.CoreMatchers.`is` as isEqualTo

class ChannelTest {

    private lateinit var mockServer: DefaultMockServer

    private lateinit var sut: RocketChatClient

    @Mock
    private lateinit var tokenProvider: TokenRepository

    private val authToken = Token("userId", "authToken")

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        mockServer = DefaultMockServer()
        mockServer.start()

        val client = OkHttpClient()
        sut = RocketChatClient.create {
            httpClient = client
            restUrl = mockServer.url("/")
            tokenRepository = this@ChannelTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }
        Mockito.`when`(tokenProvider.get()).thenReturn(authToken)
    }

    @Test
    fun `createChannel() should successfully create new channel`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.create")
                .andReturn(200, CREATE_CHANNEL_SUCCESS)
        runBlocking {
            val createChannel = sut.createChannel(roomType = RoomType.CHANNEL, name = "duplicate", usersList = listOf("aniket03"), readOnly = false)
            assertThat(createChannel.status, isEqualTo(true))
        }
    }

    @After
    fun shutdown(){
        mockServer.shutdown()
    }
}
