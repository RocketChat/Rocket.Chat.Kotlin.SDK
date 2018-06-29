package chat.rocket.core.internal

import chat.rocket.common.model.RoomType
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import chat.rocket.core.model.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.OkHttpClient
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.hamcrest.CoreMatchers.`is` as isEqualTo

class RoomListAdapterTest {

    lateinit var moshi: Moshi

    @Mock
    private lateinit var tokenProvider: TokenRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        // Just to initialize Moshi
        val client = OkHttpClient()
        val rocket = RocketChatClient.create {
            httpClient = client
            restUrl = "http://8.8.8.8"
            userAgent = "Rocket.Chat.Kotlin.SDK"
            tokenRepository = tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        moshi = rocket.moshi
    }

    @Test
    fun `should filter invalid rooms`() {
        val type = Types.newParameterizedType(List::class.java, Room::class.java)
        val adapter = moshi.adapter<List<Room>>(type)
        val rooms = adapter.fromJson(ROOMS_TEST1)!!
        assertThat(rooms.size, isEqualTo(4))
        assertThat(rooms[0].id, isEqualTo("GENERAL"))
        assert(rooms[0].type is RoomType.Channel)
        assertThat(rooms[1].id, isEqualTo("GENERAL2"))
        assert(rooms[1].type is RoomType.PrivateGroup)
        assertThat(rooms[2].id, isEqualTo("GENERAL3"))
        assert(rooms[2].type is RoomType.LiveChat)
        assertThat(rooms[3].id, isEqualTo("GENERAL4"))
        assert(rooms[3].type is RoomType.Custom)
    }

    @Test
    fun `should return empty array when all entries are invalid`() {
        val type = Types.newParameterizedType(List::class.java, Room::class.java)
        val adapter = moshi.adapter<List<Room>>(type)
        val rooms = adapter.fromJson(ROOMS_TEST2)!!
        assertThat(rooms.isEmpty(), isEqualTo(true))
    }
}

const val ROOMS_TEST1 = "[{\"_id\":\"GENERAL\",\"t\":\"c\"},{\"_id\":\"GENERAL2\",\"t\":\"p\"},{\"_id\":\"GENERAL3\",\"t\":\"l\"},{\"_id\":\"GENERAL4\",\"t\":\"v\"},{\"_id\":\"GENERAL5\"},{\"t\":\"c\"}]"
const val ROOMS_TEST2 = "[{},{},{},{}]"