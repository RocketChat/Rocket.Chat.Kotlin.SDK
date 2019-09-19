package chat.rocket.core.internal

import chat.rocket.common.model.RoomType
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.TokenRepository
import chat.rocket.core.createRocketChatClient
import chat.rocket.core.model.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.OkHttpClient
import org.hamcrest.CoreMatchers.`is` as isEqualTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RoomListAdapterTest {
    lateinit var moshi: Moshi
    @Mock private lateinit var tokenProvider: TokenRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        // Just to initialize Moshi
        val client = OkHttpClient()
        val rocket = createRocketChatClient {
            httpClient = client
            restUrl = "http://8.8.8.8"
            userAgent = "Rocket.Chat.Kotlin.SDK"
            tokenRepository = tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        moshi = rocket.moshi
    }

    @Test
    fun `should assert valid rooms`() {
        val type = Types.newParameterizedType(List::class.java, Room::class.java)
        val adapter = moshi.adapter<List<Room>>(type)
        val rooms = adapter.fromJson(ROOMS_TEST)
        assertThat(rooms?.size, isEqualTo(5))
        assertThat(rooms?.get(0)?.id, isEqualTo("GENERAL"))
        assert(rooms?.get(0)?.type is RoomType.Channel)
    }
}

const val ROOMS_TEST = "[{\"_id\":\"GENERAL\",\"t\":\"c\"},{\"_id\":\"GENERAL2\",\"t\":\"p\"},{\"_id\":\"GENERAL3\",\"t\":\"l\"},{\"_id\":\"GENERAL4\",\"t\":\"v\"},{\"_id\":\"GENERAL5\"},{\"t\":\"c\"}]"
