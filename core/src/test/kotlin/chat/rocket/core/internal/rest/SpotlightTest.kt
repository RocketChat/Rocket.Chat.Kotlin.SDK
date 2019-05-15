package chat.rocket.core.internal.rest

import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import chat.rocket.core.model.SpotlightResult
import io.fabric8.mockwebserver.DefaultMockServer
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.hamcrest.CoreMatchers.`is` as isEqualTo

class SpotlightTest {
    private lateinit var mockServer: DefaultMockServer
    private lateinit var sut: RocketChatClient
    @Mock private lateinit var tokenProvider: TokenRepository
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
            tokenRepository = this@SpotlightTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        Mockito.`when`(tokenProvider.get(sut.url)).thenReturn(authToken)
    }

    @Test
    fun `spotlight() should return correct result`() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/spotlight?query=aa")
                .andReturn(200, SPOTLIGHT_OK)
                .once()

        runBlocking {
            val spotlight = sut.spotlight("aa")

            assertThat(spotlight, isA(SpotlightResult::class.java))
            assertThat(spotlight.users.size, isEqualTo(5))
            assertThat(spotlight.rooms.size, isEqualTo(5))
            with(spotlight.users[0]) {
                assertThat(id, isEqualTo("chhtYqts4toAbrZa5"))
                assertThat(name, isEqualTo("Juli Aaron"))
                assertThat(username, isEqualTo(".JULIUS."))
            }

            with(spotlight.rooms[0]) {
                assertThat(id, isEqualTo("s6To7BxcMCz7NhGbs"))
                assertThat(name, isEqualTo("....aaa"))
            }
        }
    }
}