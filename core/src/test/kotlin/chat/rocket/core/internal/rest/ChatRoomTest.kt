package chat.rocket.core.internal.rest

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
            userAgent = "Rocket.Chat.Kotlin.SDK"
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
    fun `markAsUnread() should succeed without throwing`() {
        mockServer.expect()
            .post()
            .withPath("/api/v1/subscriptions.unread")
            .andReturn(200, SUCCESS)
            .once()

        runBlocking {
            sut.markAsUnread(roomId = "GENERAL")
        }
    }

    @Test(expected = RocketChatException::class)
    fun `markAsUnread() should fail with RocketChatAuthException if not logged in`() {
        mockServer.expect()
            .post()
            .withPath("/api/v1/subscriptions.unread")
            .andReturn(401, MUST_BE_LOGGED_ERROR)
            .once()

        runBlocking {
            sut.markAsUnread(roomId = "GENERAL")
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
            val members = sut.getMembers(roomId = "GENERAL", roomType = RoomType.Channel(), offset = 0, count = 1)
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
            sut.getMembers(roomId = "GENERAL", roomType = RoomType.Channel(), offset = 0, count = 1)
        }
    }

    // TODO Fix test
//    @Test
//    fun `getMentions() should succeed without throwing`() {
//        mockServer.expect()
//            .get()
//            .withPath("/api/v1/channels.getAllUserMentionsByChannel?roomId=GENERAL&offset=0&count=1")
//            .andReturn(200, MENTIONS_OK)
//            .once()
//
//        runBlocking {
//            val mentions = sut.getMentions(roomId = "GENERAL", offset = 0, count = 1)
//            System.out.println("Mentions: $mentions")
//        }
//    }

    @Test(expected = RocketChatException::class)
    fun `getMentions() should fail with RocketChatAuthException if not logged in`() {
        mockServer.expect()
            .get()
            .withPath("/api/v1/channels.getAllUserMentionsByChannel?roomId=GENERAL&offset=0&count=1")
            .andReturn(401, MUST_BE_LOGGED_ERROR)
            .once()

        runBlocking {
            sut.getMentions(roomId = "GENERAL", offset = 0, count = 1)
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

    @Test
    fun `leaveChat() should succeed without throwing`() {
        mockServer.expect()
            .post()
            .withPath("/api/v1/channels.leave")
            .andReturn(200, SUCCESS)
            .once()

        runBlocking {
            val result = sut.leaveChat(roomId = "GENERAL", roomType = roomTypeOf(RoomType.CHANNEL))
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
            val result = sut.leaveChat(roomId = "GENERAL", roomType = roomTypeOf(RoomType.CHANNEL))
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
            val result = sut.setTopic(
                roomId = "GENERAL", roomType = roomTypeOf(RoomType.CHANNEL),
                topic = "New Topic"
            )
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
            val result = sut.setDescription(
                roomId = "GENERAL", roomType = roomTypeOf(RoomType.CHANNEL),
                description = "New Description"
            )
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
            val result = sut.setAnnouncement(
                roomId = "GENERAL", roomType = roomTypeOf(RoomType.CHANNEL),
                announcement = "New Announcement"
            )
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
            val result = sut.setReadOnly(
                roomId = "GENERAL", roomType = roomTypeOf(RoomType.CHANNEL),
                readOnly = true
            )
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
            val result = sut.setType(
                roomId = "GENERAL", roomType = roomTypeOf(RoomType.CHANNEL),
                type = "c"
            )
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
            val result = sut.rename(roomId = "GENERAL", roomType = roomTypeOf(RoomType.CHANNEL), newName = "name")
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
            val result = sut.setJoinCode(
                roomId = "GENERAL", roomType = roomTypeOf(RoomType.CHANNEL),
                joinCode = "some_password"
            )
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
            val result = sut.archive(
                roomId = "GENERAL", roomType = roomTypeOf(RoomType.CHANNEL),
                archiveRoom = true
            )
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
            val result = sut.archive(
                roomId = "GENERAL", roomType = roomTypeOf(RoomType.CHANNEL),
                archiveRoom = false
            )
            assertTrue(result)
        }
    }

    @Test
    fun `hide() (as false) should succeed without throwing`() {
        mockServer.expect()
            .post()
            .withPath("/api/v1/channels.open")
            .andReturn(200, SUCCESS)
            .once()

        runBlocking {
            val result = sut.hide(
                roomId = "GENERAL", roomType = roomTypeOf(RoomType.CHANNEL),
                hideRoom = false
            )
            assertTrue(result)
        }
    }

    @Test
    fun `hide() (as true) should succeed without throwing`() {
        mockServer.expect()
            .post()
            .withPath("/api/v1/channels.close")
            .andReturn(200, SUCCESS)
            .once()

        runBlocking {
            val result = sut.hide(
                roomId = "GENERAL", roomType = roomTypeOf(RoomType.CHANNEL),
                hideRoom = true
            )
            assertTrue(result)
        }
    }

    @Test
    fun `favorite() (as false) should succeed without throwing`() {
        mockServer.expect()
            .post()
            .withPath("/api/v1/rooms.favorite")
            .andReturn(200, SUCCESS)
            .once()

        runBlocking {
            val result = sut.favorite(roomId = "GENERAL", favorite = false)
            assertTrue(result)
        }
    }

    @Test
    fun `favorite() (as true) should succeed without throwing`() {
        mockServer.expect()
            .post()
            .withPath("/api/v1/rooms.favorite")
            .andReturn(200, SUCCESS)
            .once()

        runBlocking {
            val result = sut.favorite(roomId = "GENERAL", favorite = true)
            assertTrue(result)
        }
    }

    @Test
    fun `searchMessages() should succeed without throwing`() {
        mockServer.expect()
            .get()
            .withPath("/api/v1/chat.search?roomId=GENERAL&searchText=test")
            .andReturn(200, MESSAGES_OK)
            .once()

        runBlocking {
            val messages = sut.searchMessages(roomId = "GENERAL", searchText = "test")
            System.out.println("Messages: $messages")
        }
    }

    @Test(expected = RocketChatException::class)
    fun `searchMessages() should fail with RocketChatAuthException if not logged in`() {
        mockServer.expect()
            .get()
            .withPath("/api/v1/chat.search?roomId=GENERAL&searchText=test")
            .andReturn(401, MUST_BE_LOGGED_ERROR)
            .once()

        runBlocking {
            sut.searchMessages(roomId = "GENERAL", searchText = "test")
        }
    }
}