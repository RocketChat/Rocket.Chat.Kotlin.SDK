package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatAuthException
import chat.rocket.common.RocketChatException
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import io.fabric8.mockwebserver.DefaultMockServer
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.OkHttpClient
import org.hamcrest.CoreMatchers.instanceOf
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

    @Test
    fun `createChannel() should fail as a result of duplicate channel`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.create")
                .andReturn(400, FAIL_DUPLICATE_CHANNEL)

        runBlocking {
            try {
                val createChannel = sut.createChannel(roomType = RoomType.CHANNEL, name = "elf", usersList = listOf("aniket03"), readOnly = false)
                throw RocketChatException("unreachable code")
            } catch (ex: Exception) {
                assertThat(ex.message, isEqualTo("duplicate_channel"))
            }
        }

    }

    @Test
    fun `createChannel() should fail with RocketChatAuthException if not logged in`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.create")
                .andReturn(401, MUST_BE_LOGGED_ERROR)
                .once()

        runBlocking {
            try {
                sut.createChannel(roomType = RoomType.CHANNEL, name = "elf", usersList = listOf("aniket03"), readOnly = false)
                throw RocketChatException("unreachable code")
            } catch (ex: Exception) {
                assertThat(ex, isEqualTo(instanceOf(RocketChatAuthException::class.java)))
                assertThat(ex.message, isEqualTo("Unauthorized"))
            }
        }
    }

    @After
    fun shutdown() {
        mockServer.shutdown()
    }
}
