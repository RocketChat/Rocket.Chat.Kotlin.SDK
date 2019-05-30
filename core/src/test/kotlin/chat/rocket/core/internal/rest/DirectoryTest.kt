package chat.rocket.core.internal.rest

import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import io.fabric8.mockwebserver.DefaultMockServer
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class DirectoryTest {
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
            tokenRepository = this@DirectoryTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        Mockito.`when`(tokenProvider.get(sut.url)).thenReturn(authToken)
    }

    @Test
    fun `directory() should return correct result for local users`() {
        mockServer.expect()
            .get()
            // /api/v1/directory?query={\"text\":\"rocket\",\"type\":\"users\",\"workspace\":\"local\"}&offset=0&count=1&sort={\"username\":1}
            .withPath("/api/v1/directory?query=%7B%22text%22%3A%22rocket%22%2C%22type%22%3A%22users%22%2C%22workspace%22%3A%22local%22%7D&offset=0&count=1&sort=%7B%22username%22%3A1%7D")
            .andReturn(200, DIRECTORY_USERS_OK)
            .once()

        runBlocking {
            val directory = sut.directory(
                "rocket",
                DirectoryRequestType.Users(),
                DirectoryWorkspaceType.Local(),
                offset = 0,
                count = 1
            )
            assertThat(directory.result[0].id, CoreMatchers.`is`("jRca8kibJx8NkLJxt"))
        }
    }

    @Test
    fun `directory() should return correct result for channels`() {
        mockServer.expect()
            .get()
            // /api/v1/directory?query={\"text\":\"gene\",\"type\":\"channels\"}
            .withPath("/api/v1/directory?query=%7B%22text%22%3A%22gene%22%2C%22type%22%3A%22channels%22%7D&offset=0&count=1&sort=%7B%22usersCount%22%3A-1%7D")
            .andReturn(200, DIRECTORY_CHANNELS_OK)
            .once()

        runBlocking {
            val directory = sut.directory("gene", DirectoryRequestType.Channels(), offset = 0, count = 1)
            assertThat(directory.result[0].id, CoreMatchers.`is`("GENERAL"))
        }
    }

    @After
    fun shutdown() {
        mockServer.shutdown()
    }
}