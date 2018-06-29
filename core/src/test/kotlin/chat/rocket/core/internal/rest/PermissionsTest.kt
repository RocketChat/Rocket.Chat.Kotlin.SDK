package chat.rocket.core.internal.rest

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

class PermissionsTest {

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
            tokenRepository = this@PermissionsTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        Mockito.`when`(tokenProvider.get(sut.url)).thenReturn(authToken)
    }

    @Test
    fun `permissions() should return a list of Permission objects`() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/permissions")
                .andReturn(200, PERMISSIONS_OK)
                .once()

        runBlocking {
            val permissions = sut.permissions()
            assertThat(permissions.size, isEqualTo(81))
            with(permissions[0]) {
                assertThat(id, isEqualTo("access-mailer"))
                assertThat(roles.size, isEqualTo(1))
                assertThat(roles[0], isEqualTo("admin"))
            }

            with(permissions[7]) {
                assertThat(id, isEqualTo("add-user-to-any-p-room"))
                assertThat(roles.size, isEqualTo(0))
            }

            with(permissions[9]) {
                assertThat(id, isEqualTo("archive-room"))
                assertThat(roles.size, isEqualTo(2))
                assertThat(roles[0], isEqualTo("admin"))
                assertThat(roles[1], isEqualTo("owner"))
            }

            with(permissions[80]) {
                assertThat(id, isEqualTo("manage-apps"))
                assertThat(roles.size, isEqualTo(1))
                assertThat(roles[0], isEqualTo("admin"))
            }
        }
    }

    @After
    fun shutdown() {
        mockServer.shutdown()
    }
}