package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.common.model.RoomType
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
import kotlin.test.assertTrue

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

    @Test
    fun `getMembers() should succeed without throwing`() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/channels.members?roomId=GENERAL&offset=0&count=1")
                .andReturn(200, MEMBERS_OK)
                .once()

        runBlocking {
            val members = sut.getMembers(roomId="GENERAL", roomType = RoomType.CHANNEL, offset = 0, count = 1)
            System.out.println("Members: $members")
        }
    }

    @Test(expected = RocketChatException::class)
    fun `getMembers() should fail with RocketChatAuthException if not logged in`() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/channels.members?roomId=GENERAL&offset=0")
                .andReturn(401, MUST_BE_LOGGED_ERROR)
                .once()

        runBlocking {
            sut.getMembers(roomId="GENERAL", roomType = RoomType.CHANNEL, offset = 0, count = 1)
        }
    }

    @Test
    fun `joinChat() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.join")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.joinChat(roomId="GENERAL")
            assertTrue(result)
        }
    }
}