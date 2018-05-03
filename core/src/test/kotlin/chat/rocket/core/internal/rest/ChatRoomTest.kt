package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatApiException
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
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import kotlin.test.assertTrue

class ChatRoomTest {
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
            tokenRepository = this@ChatRoomTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        Mockito.`when`(tokenProvider.get(sut.url)).thenReturn(authToken)
    }

    @Test
    fun `markAsRead() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/subscriptions.read")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            sut.markAsRead(roomId = "GENERAL")
        }
    }

    @Test(expected = RocketChatException::class)
    fun `markAsRead() should fail with RocketChatAuthException if not logged in`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/subscriptions.read")
                .andReturn(401, MUST_BE_LOGGED_ERROR)
                .once()

        runBlocking {
            sut.markAsRead(roomId = "GENERAL")
        }
    }

    @Test
    fun `getMembers() should succeed without throwing`() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/channels.members?roomId=GENERAL&offset=0&count=1")
                .andReturn(200, MEMBERS_OK)
                .once()

        runBlocking {
            val members = sut.getMembers(roomId = "GENERAL", roomType = RoomType.CHANNEL, offset = 0, count = 1)
            System.out.println("Members: $members")
        }
    }

    @Test(expected = RocketChatException::class)
    fun `getMembers() should fail with RocketChatAuthException if not logged in`() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/channels.members?roomId=GENERAL&offset=0")
                .andReturn(401, MUST_BE_LOGGED_ERROR)
                .once()

        runBlocking {
            sut.getMembers(roomId = "GENERAL", roomType = RoomType.CHANNEL, offset = 0, count = 1)
        }
    }

    @Test
    fun `joinChat() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.join")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.joinChat(roomId = "GENERAL")
            assertTrue(result)
        }
    }

<<<<<<< HEAD
    @Test
    fun `leaveChat() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.leave")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.leaveChat(roomId = "GENERAL", roomType = RoomType.CHANNEL)
            assertTrue(result)
        }
    }

    @Test(expected = RocketChatException::class)
    fun `leaveChat() should fail with RocketChatAuthException if not logged in`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.leave")
                .andReturn(401, MUST_BE_LOGGED_ERROR)
                .once()

        runBlocking {
            val result = sut.leaveChat(roomId = "GENERAL", roomType = RoomType.CHANNEL)
            assertTrue(result)
        }
    }

    @Test
    fun `setTopic() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.setTopic")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.setTopic(roomId = "GENERAL", roomType = RoomType.CHANNEL,
                    topic = "New Topic")
            assertTrue(result)
        }
    }

    @Test
    fun `setDescription() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.setDescription")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.setDescription(roomId = "GENERAL", roomType = RoomType.CHANNEL,
                    description = "New Description")
            assertTrue(result)
        }
    }

    @Test
    fun `setAnnouncement() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.setAnnouncement")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.setAnnouncement(roomId = "GENERAL", roomType = RoomType.CHANNEL,
                    announcement = "New Announcement")
            assertTrue(result)
        }
    }

    @Test
    fun `setReadOnly() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.setReadOnly")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.setReadOnly(roomId = "GENERAL", roomType = RoomType.CHANNEL,
                    readOnly = true)
            assertTrue(result)
        }
    }

    @Test
    fun `setType() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.setType")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.setType(roomId = "GENERAL", roomType = RoomType.CHANNEL,
                    type = "c")
            assertTrue(result)
        }
    }

    @Test
    fun `rename() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.rename")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.rename(roomId = "GENERAL", roomType = RoomType.CHANNEL, newName = "name")
            assertTrue(result)
        }
    }

    @Test
    fun `setJoinCode() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.setJoinCode")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.setJoinCode(roomId = "GENERAL", roomType = RoomType.CHANNEL,
                    joinCode = "some_password")
            assertTrue(result)
        }
    }

    @Test
    fun `archive() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.archive")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.archive(roomId = "GENERAL", roomType = RoomType.CHANNEL,
                    archiveRoom = true)
            assertTrue(result)
        }
    }

    @Test
    fun `unarchive() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.unarchive")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.archive(roomId = "GENERAL", roomType = RoomType.CHANNEL,
                    archiveRoom = false)
            assertTrue(result)
        }
    }

    @Test
    fun `open() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.open")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.hide(roomId = "GENERAL", roomType = RoomType.CHANNEL,
                    hideRoom = false)
            assertTrue(result)
        }
    }

    @Test
    fun `close() should succeed without throwing`() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/channels.close")
                .andReturn(200, SUCCESS)
                .once()

        runBlocking {
            val result = sut.hide(roomId = "GENERAL", roomType = RoomType.CHANNEL,
                    hideRoom = true)
            assertTrue(result)
=======
    // TODO Fix tests!

    @Test
    fun `queryUsers() should succeed without throwing`() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/users.list?query=%7B%20%22name%22%3A%20%7B%20%22%5Cu0024regex%22%3A%20%22g%22%20%7D%20%7D")
                .andReturn(200, QUERY_USERS_SUCCESS)
                .once()

        runBlocking {
            val result = sut.queryUsers("g")
        }
    }

    @Test(expected = RocketChatAuthException::class)
    fun `queryUsers() should fail with RocketChatAuthException if not logged in`() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/users.list?query=%7B%20%22name%22%3A%20%7B%20%22%5Cu0024regex%22%3A%20%22g%22%20%7D%20%7D")
                .andReturn(401, MUST_BE_LOGGED_ERROR)
                .once()

        runBlocking {
            val result = sut.queryUsers("g")
        }
    }

    //request fails because the query param is malformed for eg '{ "name": { "$dummy": "g" } }'
    // instead of '{ "name": { "$regex": "g" } }'
    @Test(expected = RocketChatApiException::class)
    fun `queryUsers() should fail because of incorrect param`() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/users.list?query=%7B%20%22name%22%3A%20%7B%20%22%5Cu0024regex%22%3A%20%22g%22%20%7D%20%7D")
                .andReturn(400, INCORRECT_PARAM_PROVIDED)
                .once()

        runBlocking {
            val result = sut.queryUsers("g")
>>>>>>> 9f7cd2d628bcaf69ed4f26038ef6eb3fe02f4890
        }
    }
}