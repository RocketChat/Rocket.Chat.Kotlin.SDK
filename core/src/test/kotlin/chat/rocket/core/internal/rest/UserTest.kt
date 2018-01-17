package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatApiException
import chat.rocket.common.RocketChatAuthException
import chat.rocket.common.model.BaseUser
import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import io.fabric8.mockwebserver.DefaultMockServer
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.OkHttpClient
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.hamcrest.CoreMatchers.`is` as isEqualTo

class UserTest {

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
            tokenRepository = this@UserTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        Mockito.`when`(tokenProvider.get()).thenReturn(authToken)
    }

    @Test
    fun `me() Should return user information for the used Access Token`() {
        mockServer.expect().get().withPath("/api/v1/me").andReturn(200, ME_SUCCESS).once()

        runBlocking {
            val user = sut.me()

            assertThat(user.username, isEqualTo("testuser"))
            assertThat(user.name, isEqualTo("testuser"))
            assertThat(user.active, isEqualTo(true))
            assertThat(user.status, isEqualTo(BaseUser.Status.OFFLINE))
            assertThat(user.statusConnection, isEqualTo(BaseUser.Status.OFFLINE))
            assertThat(user.utcOffset, isEqualTo((-3).toFloat()))
            assertThat(user.emails?.size, isEqualTo(1))
            val email = user.emails!![0]
            assertThat(email.address, isEqualTo("testuser@test.com"))
            assertThat(email.verified, isEqualTo(false))
        }
    }

    @Test
    fun `me() should return 401 Unauthorized with invalid Token`() {
        mockServer.expect().get().withPath("/api/v1/me").andReturn(401, ME_UNAUTHORIZED).once()

        runBlocking {
            try {
                sut.me()

                throw RuntimeException("unreachable code")
            } catch (ex: Exception) {
                assertThat(ex, isEqualTo(CoreMatchers.instanceOf(RocketChatAuthException::class.java)))
                assertThat(ex.message, isEqualTo("You must be logged in to do this."))
            }
        }
    }

    @Test
    fun `chatRooms() should return users chatrooms`() {
        mockServer.expect()
                .get().withPath("/api/v1/rooms.get?updatedAt=1970-01-01T00:00:00.000Z").andReturn(200, ROOMS_OK).once()
        mockServer.expect()
                .get().withPath("/api/v1/subscriptions.get?updatedAt=1970-01-01T00:00:00.000Z").andReturn(200, SUBSCRIPTIONS_OK).once()

        runBlocking {
            val rooms = sut.chatRooms()
            System.out.println("Rooms: $rooms")
        }
    }

    @Test
    fun `updateEmail() should succeed with valid parameters` () {
        mockServer.expect()
                .post()
                .withPath("/api/v1/users.update")
                .andReturn(200, USER_UPDATE_SUCCESS)
                .once()

        runBlocking {
            val user = sut.updateEmail("userId", "test@email.com")
            assertThat(user.id, isEqualTo("userId"))
        }
    }

    @Test
    fun `updateEmail() should fail with RocketChatApiException if email is already in use`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/users.update")
                .andReturn(403, FAIL_EMAIL_IN_USE)
                .once()

        runBlocking {
            try {
                sut.updateEmail("userId", "test@email.com")
                throw RuntimeException("unreachable code")
            } catch (ex: Exception) {
                assertThat(ex, isEqualTo(instanceOf(RocketChatApiException::class.java)))
                assertThat(ex.message, isEqualTo("Email already exists. [403]"))
                val apiException = ex as RocketChatApiException
                assertThat(apiException.errorType, isEqualTo("403"))
            }
        }
    }

    @Test
    fun `updateName() should succeed with valid parameters` () {
        mockServer.expect()
                .post()
                .withPath("/api/v1/users.update")
                .andReturn(200, USER_UPDATE_SUCCESS)
                .once()

        runBlocking {
            val user = sut.updateName("userId", "New name")
            assertThat(user.id, isEqualTo("userId"))
            assertThat(user.name, isEqualTo("New name"))
        }
    }

    @Test
    fun `updatePassword() should succeed with valid parameters` () {
        mockServer.expect()
                .post()
                .withPath("/api/v1/users.update")
                .andReturn(200, USER_UPDATE_SUCCESS)
                .once()

        runBlocking {
            val user = sut.updatePassword("userId", "new-bcrypt-password")
            assertThat(user.id, isEqualTo("userId"))
        }
    }

    @Test
    fun `updateUsername() should succeed with valid parameters` () {
        mockServer.expect()
                .post()
                .withPath("/api/v1/users.update")
                .andReturn(200, USER_UPDATE_SUCCESS)
                .once()

        runBlocking {
            val user = sut.updateUsername("userId", "new-username")
            assertThat(user.id, isEqualTo("userId"))
            assertThat(user.username, isEqualTo("new-username"))
        }
    }

    @Test
    fun `updateUsername() should fail with RocketChatApiException if username is already in use`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/users.update")
                .andReturn(403, FAIL_EMAIL_IN_USE)
                .once()

        runBlocking {
            try {
                sut.updateUsername("userId", "testuser")
                throw RuntimeException("unreachable code")
            } catch (ex: Exception) {
                assertThat(ex, isEqualTo(instanceOf(RocketChatApiException::class.java)))
                val apiException = ex as RocketChatApiException
                assertThat(apiException.errorType, isEqualTo("403"))
                assertThat(apiException.message, isEqualTo("Email already exists. [403]"))
            }
        }
    }

    @After
    fun shutdown() {
        mockServer.shutdown()
    }
}