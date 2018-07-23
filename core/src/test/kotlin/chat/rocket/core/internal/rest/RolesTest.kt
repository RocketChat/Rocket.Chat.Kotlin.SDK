package chat.rocket.core.internal.rest

import chat.rocket.common.model.RoomType
import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import io.fabric8.mockwebserver.DefaultMockServer
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.OkHttpClient
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.hamcrest.CoreMatchers.`is` as isEqualTo

class RolesTest {
    private val ROLES_OK = """
    {
        "roles": [
            {
                "rid": "BaE62jfDLXK3Xo6BA",
                "u": {
                    "_id": "XLH8FHfZfrTodM7k9",
                    "username": "matheus.cardoso",
                    "name": null
                },
                "roles": [
                    "owner"
                ],
                "_id": "GA5msx4eyYPGxn3cT"
            },
            {
                "rid": "BaE62jfDLXK3Xo6BA",
                "u": {
                    "_id": "BkNkw3iKgNyhMbPyW",
                    "username": "ronnie.dio",
                    "name": "Ronnie James Dio"
                },
                "roles": [
                    "moderator"
                ],
                "_id": "ehPuGyZBedznJsQHp"
            }
        ],
        "success": true
    }
    """.trimIndent()

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
            tokenRepository = this@RolesTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        Mockito.`when`(tokenProvider.get(sut.url)).thenReturn(authToken)
    }

    @Test
    fun `chatRoomRoles() should return correct result for groups`() {
        mockServer.expect()
            .get()
            .withPath("/api/v1/groups.roles?roomName=private-general")
            .andReturn(200, ROLES_OK)
            .once()

        runBlocking {
            val chatRoomRoles = sut.chatRoomRoles(roomType = RoomType.PrivateGroup(), roomName = "private-general")

            println(chatRoomRoles)

            assertThat(chatRoomRoles.size, isEqualTo(2))

            with(chatRoomRoles[0]) {
                assertThat(id, isEqualTo("GA5msx4eyYPGxn3cT"))
                assertThat(roomId, isEqualTo("BaE62jfDLXK3Xo6BA"))
                with(user) {
                    assertThat(id, isEqualTo("XLH8FHfZfrTodM7k9"))
                    assertThat(username, isEqualTo("matheus.cardoso"))
                    assertThat(name == null, isEqualTo(true))
                }

                assertThat(roles.size, isEqualTo(1))
                assertThat(roles.first(), isEqualTo("owner"))
            }

            with(chatRoomRoles[1]) {
                assertThat(roomId, isEqualTo("BaE62jfDLXK3Xo6BA"))
                assertThat(id, isEqualTo("ehPuGyZBedznJsQHp"))
                with(user) {
                    assertThat(id, isEqualTo("BkNkw3iKgNyhMbPyW"))
                    assertThat(username, isEqualTo("ronnie.dio"))
                    assertThat(name, isEqualTo("Ronnie James Dio"))
                }

                assertThat(roles.size, isEqualTo(1))
                assertThat(roles.first(), isEqualTo("moderator"))
            }
        }
    }

    @Test
    fun `chatRoomRoles() should return correct result for channels`() {
        mockServer.expect()
            .get()
            .withPath("/api/v1/channels.roles?roomName=general")
            .andReturn(200, ROLES_OK)
            .once()

        runBlocking {
            val chatRoomRoles = sut.chatRoomRoles(roomType = RoomType.Channel(), roomName = "general")

            assertThat(chatRoomRoles.size, isEqualTo(2))

            with(chatRoomRoles[0]) {
                assertThat(roomId, isEqualTo("BaE62jfDLXK3Xo6BA"))
                assertThat(id, isEqualTo("GA5msx4eyYPGxn3cT"))
                with(user) {
                    assertThat(id, isEqualTo("XLH8FHfZfrTodM7k9"))
                    assertThat(username, isEqualTo("matheus.cardoso"))
                    assertThat(name == null, isEqualTo(true))
                }

                assertThat(roles.size, isEqualTo(1))
                assertThat(roles.first(), isEqualTo("owner"))
            }

            with(chatRoomRoles[1]) {
                assertThat(roomId, isEqualTo("BaE62jfDLXK3Xo6BA"))
                assertThat(id, isEqualTo("ehPuGyZBedznJsQHp"))
                with(user) {
                    assertThat(id, isEqualTo("BkNkw3iKgNyhMbPyW"))
                    assertThat(username, isEqualTo("ronnie.dio"))
                    assertThat(name, isEqualTo("Ronnie James Dio"))
                }

                assertThat(roles.size, isEqualTo(1))
                assertThat(roles.first(), isEqualTo("moderator"))
            }
        }
    }
}