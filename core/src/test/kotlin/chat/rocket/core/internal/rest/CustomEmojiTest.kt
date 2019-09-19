package chat.rocket.core.internal.rest

import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import chat.rocket.core.createRocketChatClient
import io.fabric8.mockwebserver.DefaultMockServer
import okhttp3.OkHttpClient
import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

const val EMOJI_CUSTOM_RESPONSE =
    """
{
    "emojis":{
       "update":[
          {
             "_id":"2cgzHwKP6Cq3iZCob",
             "name":"troll",
             "aliases":[
 
             ],
             "extension":"jpg",
             "_updatedAt":"2017-01-27T19:52:20.427Z"
          }
       ],
       "remove":[]
    },
    "success":true
 }   
"""

class CustomEmojiTest {
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
        sut = createRocketChatClient {
            httpClient = client
            restUrl = mockServer.url("/")
            userAgent = "Rocket.Chat.Kotlin.SDK"
            tokenRepository = this@CustomEmojiTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        Mockito.`when`(tokenProvider.get(sut.url)).thenReturn(authToken)
    }
    /* FIXME
    @Test
    fun `getCustomEmojis() should return list of custom emojis`() {
        mockServer.expect()
            .get()
            .withPath("/api/v1/emoji-custom.list")
            .andReturn(200, EMOJI_CUSTOM_RESPONSE)
            .once()

        runBlocking {
            sut.getCustomEmojis().update.let {
                assertThat(it.size, isEqualTo(1))
                assertThat(it[0].name, isEqualTo("troll"))
                assertThat(it[1].extension, isEqualTo("jpg"))
            }
        }
    }
    */
}
