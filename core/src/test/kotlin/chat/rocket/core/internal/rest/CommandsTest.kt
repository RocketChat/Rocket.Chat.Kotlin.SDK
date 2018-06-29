package chat.rocket.core.internal.rest

import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import chat.rocket.core.model.Command
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

class CommandsTest {
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
            tokenRepository = this@CommandsTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        Mockito.`when`(tokenProvider.get(sut.url)).thenReturn(authToken)
    }

    @Test
    fun `commands() should return a list of available commands on a server`() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/commands.list?offset=0&count=30")
                .andReturn(200, COMMANDS_LIST_OK)
                .once()

        runBlocking {
            val commands = sut.commands(offset = 0, count = 30)

            assertThat(commands.total, isEqualTo(22L))
            assertThat(commands.offset, isEqualTo(0L))
            assertThat(commands.result.size, isEqualTo(22))
            assertThat(commands.result.size, isEqualTo(22))

            with(commands.result[0]) {
                assertThat(command, isEqualTo("invite-all-from"))
                assertThat(clientOnly, isEqualTo(false))
            }

            with(commands.result[1]) {
                assertThat(command, isEqualTo("slackbridge-import"))
                assertThat(clientOnly, isEqualTo(false))
            }

            with(commands.result[16]) {
                assertThat(command, isEqualTo("me"))
                assertThat(params, isEqualTo("your_message"))
                assertThat(description, isEqualTo("Displays_action_text"))
                assertThat(clientOnly, isEqualTo(false))
            }

            with(commands.result[20]) {
                assertThat(command, isEqualTo("topic"))
                assertThat(params, isEqualTo("Slash_Topic_Params"))
                assertThat(description, isEqualTo("Slash_Topic_Description"))
                assertThat(clientOnly, isEqualTo(false))
            }

            with(commands.result[21]) {
                assertThat(command, isEqualTo("unarchive"))
                assertThat(clientOnly, isEqualTo(false))
            }
        }
    }

    @Test
    fun `runCommand() should signal the backend to run the specified command`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/commands.run")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.runCommand(Command("unmute", "@user123"), "ByehQjC44FwMeiLbX")
            assertThat(result, isEqualTo(true))
        }
    }

    @After
    fun shutdown() {
        mockServer.shutdown()
    }
}