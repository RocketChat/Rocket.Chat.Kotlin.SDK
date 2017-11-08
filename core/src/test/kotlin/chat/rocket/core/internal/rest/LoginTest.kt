package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatApiException
import chat.rocket.common.RocketChatAuthException
import chat.rocket.common.RocketChatException
import chat.rocket.common.RocketChatInvalidResponseException
import chat.rocket.common.model.Token
import chat.rocket.common.model.User
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenProvider
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.timeout
import com.nhaarman.mockito_kotlin.verify
import com.squareup.moshi.JsonEncodingException
import io.fabric8.mockwebserver.DefaultMockServer
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.*
import org.hamcrest.CoreMatchers.`is` as isEqualTo

class LoginTest {

    private lateinit var mockServer: DefaultMockServer

    private lateinit var sut: RocketChatClient

    @Mock
    private lateinit var tokenProvider: TokenProvider

    private val authToken = Token("userId", "authToken")

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        mockServer = DefaultMockServer()
        mockServer.start()

        val baseUrl = HttpUrl.parse(mockServer.url("/"))
        val client = OkHttpClient()
        sut = RocketChatClient.create {
            httpClient = client
            restUrl = baseUrl!!
            websocketUrl = "not needed"
            tokenProvider = this@LoginTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        `when`(tokenProvider.get()).thenReturn(authToken)
    }

    @Test
    fun `Login should succeed with right credentials`() {
        val success: (Token) -> Unit = mock()
        val error: (RocketChatException) -> Unit = mock()

        mockServer.expect()
                .post()
                .withPath("/api/v1/login")
                .andReturn(200, LOGIN_SUCCESS)
                .once()

        sut.login("username", "password", success, error)

        verify(success, timeout(2000).times(1)).invoke(check {
            assertThat(it, isEqualTo(authToken))
            assertThat(it.userId, isEqualTo("userId"))
            assertThat(it.authToken, isEqualTo("authToken"))
        })

        verify(tokenProvider).save(check {
            assertThat(it.userId, isEqualTo("userId"))
            assertThat(it.authToken, isEqualTo("authToken"))
        })

        verify(error, never()).invoke(check { })
    }

    @Test
    fun `Login should fail with RocketChatAuthException on wrong credentials`() {
        val success: (Token) -> Unit = mock()
        val error: (RocketChatException) -> Unit = mock()

        mockServer.expect()
                .post()
                .withPath("/api/v1/login")
                .andReturn(401, LOGIN_ERROR)
                .once()

        sut.login("wronguser", "wrongpass", success, error)

        verify(error, timeout(2000).times(1)).invoke(check {
            assertThat(it, isEqualTo(instanceOf(RocketChatAuthException::class.java)))
            assertThat(it.message, isEqualTo("Unauthorized"))
        })

        verify(success, never()).invoke(check {  })
        verify(tokenProvider, never()).save(check {  })
    }

    @Test
    fun `Login should fail with RocketChatInvalidResponseException on invalid response`() {
        val success: (Token) -> Unit = mock()
        val error: (RocketChatException) -> Unit = mock()

        mockServer.expect()
                .post()
                .withPath("/api/v1/login")
                .andReturn(200, "NOT A JSON")
                .once()

        sut.login("user", "pass", success, error)

        verify(error, timeout(2000).times(1)).invoke(check {
            assertThat(it, isEqualTo(instanceOf(RocketChatInvalidResponseException::class.java)))
            assertThat(it.message, isEqualTo("Use JsonReader.setLenient(true) to accept malformed JSON at path $"))
            assertThat(it.cause, isEqualTo(instanceOf(JsonEncodingException::class.java)))
        })

        verify(success, never()).invoke(check {  })
        verify(tokenProvider, never()).save(check {  })
    }

    @Test
    fun `Login should fail with RocketChatApiException when response is not 200 OK`() {
        val success: (Token) -> Unit = mock()
        val error: (RocketChatException) -> Unit = mock()

        sut.login("user", "pass", success, error)

        verify(error, timeout(2000).times(1)).invoke(check {
            assertThat(it, isEqualTo(instanceOf(RocketChatApiException::class.java)))
        })

        verify(success, never()).invoke(check {  })
        verify(tokenProvider, never()).save(check {  })
    }

    @Test
    fun `Register should succeed with valid parameters`() {
        val success: (User) -> Unit = mock()
        val error: (RocketChatException) -> Unit = mock()

        mockServer.expect()
                .post()
                .withPath("/api/v1/users.register")
                .andReturn(200, REGISTER_SUCCESS)
                .once()

        sut.signup("test@email.com", "Test User", "testuser",
                "password", success, error)

        verify(success, timeout(DEFAULT_TIMEOUT).times(1)).invoke(check {
            val user = it
            assertThat(user.id, isEqualTo("userId"))
        })
    }

    @Test
    fun `Register should fail with RocketChatApiException if email is already in use`() {
        val success: (User) -> Unit = mock()
        val error: (RocketChatException) -> Unit = mock()

        mockServer.expect()
                .post()
                .withPath("/api/v1/users.register")
                .andReturn(403, REGISTER_FAIL_EMAIL_IN_USE)
                .once()

        sut.signup("test@email.com", "Test User", "testuser",
                "password", success, error)

        verify(error, timeout(DEFAULT_TIMEOUT).times(1)).invoke(check {
            assertThat(it, isEqualTo(instanceOf(RocketChatApiException::class.java)))
            val ex = it as RocketChatApiException
            assertThat(ex.errorType, isEqualTo("403"))
            assertThat(ex.message, isEqualTo("Email already exists. [403]"))
        })
    }

    @Test
    fun `Register should fail with RocketChatApiException if username is already in use`() {
        val success: (User) -> Unit = mock()
        val error: (RocketChatException) -> Unit = mock()

        mockServer.expect()
                .post()
                .withPath("/api/v1/users.register")
                .andReturn(403, REGISTER_FAIL_USER_IN_USE)
                .once()

        sut.signup("test@email.com", "Test User", "testuser",
                "password", success, error)

        verify(error, timeout(DEFAULT_TIMEOUT).times(1)).invoke(check {
            assertThat(it, isEqualTo(instanceOf(RocketChatApiException::class.java)))
            val ex = it as RocketChatApiException
            assertThat(ex.errorType, isEqualTo("error-field-unavailable"))
            assertThat(ex.message, isEqualTo("<strong>testuser</strong> is already in use :( [error-field-unavailable]"))
        })
    }
}