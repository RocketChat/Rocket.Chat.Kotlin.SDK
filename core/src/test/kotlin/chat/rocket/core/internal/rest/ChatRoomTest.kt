package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import io.fabric8.mockwebserver.DefaultMockServer
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class ChatRoomTest {
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
            tokenRepository = this@ChatRoomTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        Mockito.`when`(tokenProvider.get()).thenReturn(authToken)
    }

    @Test
    fun `markAsRead() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/subscriptions.read")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            sut.markAsRead(roomId="GENERAL")
        }
    }

    @Test(expected = RocketChatException::class)
    fun `markAsRead() should fail with RocketChatAuthException if not logged in`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/subscriptions.read")
                .andReturn(401, MUST_BE_LOGGED_ERROR)
                .once()

        runBlocking {
            sut.markAsRead(roomId="GENERAL")
        }
    }
}