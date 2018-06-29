package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatAuthException
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
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.hamcrest.CoreMatchers.`is` as isEqualTo

class LogoutTest {

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
            tokenRepository = this@LogoutTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        `when`(tokenProvider.get(sut.url)).thenReturn(authToken)
    }

    @Test
    fun `logout() should succeed without throwing`() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/logout")
                .andReturn(200, LOGOUT_SUCCESS)
                .once()

        runBlocking {
            sut.logout()
        }
    }

    @Test
    fun `logout() should fail with RocketChatAuthException if not logged in`() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/logout")
                .andReturn(401, MUST_BE_LOGGED_ERROR)
                .once()

        runBlocking {
            try {
                sut.logout()
            } catch (ex: Exception) {
                assertThat(ex, isEqualTo(instanceOf(RocketChatAuthException::class.java)))
                assertThat(ex.message, isEqualTo("You must be logged in to do this."))
            }
        }
    }

    @After
    fun shutdown() {
        mockServer.shutdown()
    }
}