package chat.rocket.core.internal

import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import chat.rocket.core.model.Reactions
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.hamcrest.CoreMatchers.`is` as isEqualTo

const val REACTIONS = """
{
  ":hearts:": {
    "usernames": [
      "leonardo.aramaki"
    ]
  },
  ":vulcan:": {
    "usernames": [
      "mr.spock"
    ]
  },
  ":kotlin:": {
    "usernames": [
      "andrey.breslav",
      "captain.underpants"
    ]
  }
}
"""

val REACTIONS_EMPTY = "[]"
class ReactionsAdapterTest {
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
    fun `should deserialize JSON with reactions`() {
        val adapter = moshi.adapter<Reactions>(Reactions::class.java)
        val reactions = adapter.fromJson(REACTIONS)
        assertThat(reactions!!.size, isEqualTo(3))
        assertThat(reactions[":hearts:"]!!.size, isEqualTo(1))
        assertThat(reactions[":hearts:"]!![0], isEqualTo("leonardo.aramaki"))
        assertThat(reactions[":vulcan:"]!!.size, isEqualTo(1))
        assertThat(reactions[":vulcan:"]!![0], isEqualTo("mr.spock"))
        assertThat(reactions[":kotlin:"]!!.size, isEqualTo(2))
        assertThat(reactions[":kotlin:"]!![0], isEqualTo("andrey.breslav"))
        assertThat(reactions[":kotlin:"]!![1], isEqualTo("captain.underpants"))
        assertThat(reactions.getShortNames().size, isEqualTo(3))
        assertThat(reactions.getUsernames(":vulcan:")!!.size, isEqualTo(1))
        assertThat(reactions.getUsernames(":kotlin:")!!.size, isEqualTo(2))
    }

    @Test
    fun `should deserialize empty reactions JSON`() {
        val adapter = moshi.adapter<Reactions>(Reactions::class.java)
        val reactions = adapter.fromJson(REACTIONS_EMPTY)
        assertThat(reactions!!.size, isEqualTo(0))
    }

    @Test
    fun `should serialize back to JSON string`() {
        val adapter = moshi.adapter<Reactions>(Reactions::class.java)
        val reactions = adapter.fromJson(REACTIONS)
        val reactionsJson = adapter.toJson(reactions)
        assertThat(adapter.fromJson(reactionsJson), isEqualTo(reactions))
    }
}