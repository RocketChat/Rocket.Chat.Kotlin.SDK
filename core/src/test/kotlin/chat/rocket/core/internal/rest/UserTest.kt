package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatAuthException
import chat.rocket.common.RocketChatException
import chat.rocket.common.model.BaseRoom
import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenProvider
import chat.rocket.core.model.Myself
import chat.rocket.core.model.Room
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.createinstance.createInstance
import com.nhaarman.mockito_kotlin.isNotNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.timeout
import com.nhaarman.mockito_kotlin.verify
import io.fabric8.mockwebserver.DefaultMockServer
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

import org.mockito.Mockito.never
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.hamcrest.CoreMatchers.`is` as isEqualTo

class UserTest {

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
            tokenProvider = this@UserTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        Mockito.`when`(tokenProvider.get()).thenReturn(authToken)
    }

    @Test
    fun `me() Should return user information for the used Access Token`() {
        val sucess: (Myself) -> Unit = mock()
        val error: (RocketChatException) -> Unit = mock()

        mockServer.expect().get().withPath("/api/v1/me").andReturn(200, ME_SUCCESS).once()

        sut.me(sucess, error)

        verify(sucess, timeout(DEFAULT_TIMEOUT).times(1)).invoke(check {
            assertThat(it.username, isEqualTo("testuser"))
            assertThat(it.name, isEqualTo("testuser"))
            assertThat(it.active, isEqualTo(true))
            assertThat(it.status, isEqualTo("offline"))
            assertThat(it.statusConnection, isEqualTo("offline"))
            assertThat(it.utcOffset, isEqualTo((-3).toFloat()))
            assertThat(it.emails?.size, isEqualTo(1))
            val email = it.emails!![0]
            assertThat(email.address, isEqualTo("testuser@test.com"))
            assertThat(email.verified, isEqualTo(false))
        })

        verify(error, never()).invoke(check { })
    }

    @Test
    fun `me() should return 401 Unauthorized with invalid Token`() {
        val success: (Myself) -> Unit = mock()
        val error: (RocketChatException) -> Unit = mock()

        mockServer.expect().get().withPath("/api/v1/me").andReturn(401, ME_UNAUTHORIZED).once()

        sut.me(success, error)

        verify(error, timeout(DEFAULT_TIMEOUT).times(1)).invoke(check {
            assertThat(it, isEqualTo(CoreMatchers.instanceOf(RocketChatAuthException::class.java)))
            assertThat(it.message, isEqualTo("You must be logged in to do this."))
        })

        verify(success, never()).invoke(check { })
    }

    @Test
    fun `channelSubscriptions() should return the user channel subscriptions`() {
        val rooms = ArrayList<Room>()
        var total: Long? = -1
        val error: (RocketChatException) -> Unit = mock()

        // Using a latch countdown, couldn't find a better way to verify a multi parameter lambda
        val latch = CountDownLatch(1)
        val success: (List<Room>, Long) -> Unit = { list, size ->
            rooms.addAll(list)
            total = size
            latch.countDown()
        }

        mockServer.expect()
                .get()
                .withPath("/api/v1/channels.list.joined?offset=0")
                .andReturn(200, USER_SUBSCRIPTIONS_OK)
                .once()

        sut.channelSubscriptions(0, success, error)

        latch.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)

        assertThat(total, isEqualTo(1.toLong()))
        assertThat(rooms.size, isEqualTo(1))
        with(rooms[0]) {
            assertThat(id, isEqualTo("GENERAL"))
            assertThat(timestamp, isEqualTo(1508503893778))
            assertThat(lastModified, isEqualTo(1509984120611))
            assertThat(updatedAt, isEqualTo(1510935364654))
            assertThat(name, isEqualTo("general"))
            assertThat(type, isEqualTo(BaseRoom.RoomType.PUBLIC))
            assertThat(messageCount, isEqualTo(23))
        }

        verify(error, never()).invoke(check { })
    }

    @After
    fun shutdown() {
        mockServer.shutdown()
    }
}