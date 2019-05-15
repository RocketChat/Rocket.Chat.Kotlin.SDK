package chat.rocket.core.internal

import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import chat.rocket.core.model.Reactions
import chat.rocket.core.model.Room
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.hamcrest.CoreMatchers.`is` as isEqualTo

const val REACTIONS_JSON_PAYLOAD = "{\"reactions\":{\":croissant:\":{\"usernames\":[\"test.user\",\"test.user2\"],\"names\":[\"Test User\",\"Test User 2\"]}, \":thumbsup:\":{\"usernames\":[\"test.user\",\"test.user2\"],\"names\":[\"Test User\",\"Test User 2\"]}}}"
const val REACTIONS_JSON_PAYLOAD_WITHOUT_NAME = "{\"reactions\":{\":croissant:\":{\"usernames\":[\"test.user\"]}}}"
const val REACTIONS_EMPTY_JSON_PAYLOAD = "[]"
const val REACTIONS_STREAM_ROOMS_CHANGED_PAYLOAD = "{\"fname\":\"playground\",\"t\":\"p\",\"u\":{\"_id\":\"f8LAMX3vpYc9hYi3s\",\"username\":\"filipedelimabrito\"},\"sysMes\":true,\"customFields\":{},\"name\":\"playground\",\"lastMessage\":{\"msg\":\":grin:\",\"channels\":[],\"u\":{\"name\":\"Filipe de Lima Brito\",\"_id\":\"f8LAMX3vpYc9hYi3s\",\"username\":\"filipedelimabrito\"},\"mentions\":[],\"reactions\":{\":grimacing:\":{\"usernames\":[\"filipedelimabrito\"]},\":hearts:\":{\"usernames\":[\"filipedelimabrito\"]}},\"_id\":\"aq4674o6JLBpva5Yi\",\"rid\":\"LRdPfvJr9g3gRc9qf\",\"_updatedAt\":{\"\$date\":1557344843650},\"ts\":{\"\$date\":1556658923424}},\"_id\":\"LRdPfvJr9g3gRc9qf\",\"ro\":false,\"_updatedAt\":{\"\$date\":1557344848231}}"

class ReactionsAdapterTest {
    lateinit var moshi: Moshi
    @Mock private lateinit var tokenProvider: TokenRepository

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
    fun `should deserialize JSON with reactions (with names)`() {
        val adapter = moshi.adapter<Reactions>(Reactions::class.java)
        adapter.fromJson(REACTIONS_JSON_PAYLOAD)?.let { reactions ->
            assertThat(reactions.size, isEqualTo(2))
            assertThat(reactions[":croissant:"]?.first?.size, isEqualTo(2))
            assertThat(reactions[":croissant:"]?.second?.size, isEqualTo(2))
            assertThat(reactions[":croissant:"]?.first?.get(0), isEqualTo("test.user"))
            assertThat(reactions[":croissant:"]?.second?.get(0), isEqualTo("Test User"))
        }
    }

    @Test
    fun `should deserialize JSON with reactions (without names)`() {
        val adapter = moshi.adapter<Reactions>(Reactions::class.java)
        adapter.fromJson(REACTIONS_JSON_PAYLOAD_WITHOUT_NAME)?.let { reactions ->
            assertThat(reactions.size, isEqualTo(1))
            assertThat(reactions[":croissant:"]?.first?.size, isEqualTo(1))
            assertThat(reactions[":croissant:"]?.second?.size, isEqualTo(0))
            assertThat(reactions[":croissant:"]?.first?.get(0), isEqualTo("test.user"))
        }
    }

    @Test
    fun `should deserialize JSON with reactions (streamed room)`() {
        val adapter = moshi.adapter<Room>(Room::class.java)
        adapter.fromJson(REACTIONS_STREAM_ROOMS_CHANGED_PAYLOAD)?.let { room ->
            with(room.lastMessage?.reactions) {
                assertThat(this?.size, isEqualTo(2))
                assertThat(this?.let { it[":grimacing:"]?.first?.size }, isEqualTo(1))
                assertThat(this?.let { it[":grimacing:"]?.second?.size }, isEqualTo(0))
                assertThat(this?.let { it[":grimacing:"]?.first?.get(0) }, isEqualTo("filipedelimabrito"))
            }
        }
    }

    @Test
    fun `should serialize back to JSON string (with names)`() {
        val adapter = moshi.adapter<Reactions>(Reactions::class.java)
        val reactionsFromJson = adapter.fromJson(REACTIONS_JSON_PAYLOAD)
        val reactionsToJson = adapter.toJson(reactionsFromJson)
        val reactions = adapter.fromJson(reactionsToJson)
        assertThat(reactions, isEqualTo(reactionsFromJson))
    }

    @Test
    fun `should serialize back to JSON string (without names)`() {
        val adapter = moshi.adapter<Reactions>(Reactions::class.java)
        val reactionsFromJson = adapter.fromJson(REACTIONS_JSON_PAYLOAD_WITHOUT_NAME)
        val reactionsToJson = adapter.toJson(reactionsFromJson)
        val reactions = adapter.fromJson(reactionsToJson)
        assertThat(reactions, isEqualTo(reactionsFromJson))
    }

    @Test
    fun `should deserialize empty reactions JSON`() {
        val adapter = moshi.adapter<Reactions>(Reactions::class.java)
        adapter.fromJson(REACTIONS_EMPTY_JSON_PAYLOAD)?.let { reactions ->
            assertThat(reactions.size, isEqualTo(0))
        }
    }
}