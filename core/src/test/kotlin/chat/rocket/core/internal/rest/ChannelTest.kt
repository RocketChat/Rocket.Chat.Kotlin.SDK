package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatAuthException
import chat.rocket.common.RocketChatException
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.Token
import chat.rocket.common.model.roomTypeOf
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import io.fabric8.mockwebserver.DefaultMockServer
import kotlinx.coroutines.runBlocking
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
            userAgent = "Rocket.Chat.Kotlin.SDK"
            tokenRepository = this@ChannelTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }
        Mockito.`when`(tokenProvider.get(sut.url)).thenReturn(authToken)
    }

    @Test
    fun `createChannel() should successfully create new channel`() {
        mockServer.expect()
            .post()
            .withPath("/api/v1/channels.create")
            .andReturn(200, CREATE_CHANNEL_SUCCESS)
            .once()

        runBlocking {
            sut.createChannel(
                roomType = RoomType.Channel(),
                name = "duplicate",
                usersList = listOf("aniket03"),
                readOnly = false
            )
        }
    }

    @Test
    fun `createChannel() should fail as a result of duplicate channel`() {
        mockServer.expect()
            .post()
            .withPath("/api/v1/channels.create")
            .andReturn(400, FAIL_DUPLICATE_CHANNEL)
            .once()

        runBlocking {
            try {
                sut.createChannel(
                    roomType = RoomType.Channel(),
                    name = "elf",
                    usersList = listOf("aniket03"),
                    readOnly = false
                )
                throw RocketChatException("unreachable code")
            } catch (ex: Exception) {
                assertThat(ex.message, isEqualTo("A channel with name 'elf' exists [error-duplicate-channel-name]"))
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
                sut.createChannel(
                    roomType = RoomType.Channel(),
                    name = "elf",
                    usersList = listOf("aniket03"),
                    readOnly = false
                )
                throw RocketChatException("unreachable code")
            } catch (ex: Exception) {
                assertThat(ex, isEqualTo(instanceOf(RocketChatAuthException::class.java)))
                assertThat(ex.message, isEqualTo("You must be logged in to do this."))
            }
        }
    }

    @Test
    fun `createDirectMessage() should return true and yield no exceptions`() {
        mockServer.expect()
            .post()
            .withPath("/api/v1/im.create")
            .andReturn(200, CREATE_DM_OK)
            .once()

        runBlocking {
            val result = sut.createDirectMessage("rocket.cat")
            assertThat(result.id, isEqualTo("Lymsiu4Mn6xjTAan4RtMDEYc28fQ5aHpf4"))
            assertThat(result.type, isEqualTo(roomTypeOf("d")))
            assertThat(result.usernames.size, isEqualTo(2))
            assertThat(result.usernames[0], isEqualTo("rocket.cat"))
            assertThat(result.usernames[1], isEqualTo("user.test"))
        }
    }

    @After
    fun shutdown() {
        mockServer.shutdown()
    }
}
