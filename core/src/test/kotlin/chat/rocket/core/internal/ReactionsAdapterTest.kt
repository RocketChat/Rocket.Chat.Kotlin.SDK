package chat.rocket.core.internal

import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import chat.rocket.core.model.Reaction
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
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
            tokenRepository = tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        moshi = rocket.moshi
    }

    @Test
    fun `should filter invalid rooms`() {
        val type = Types.newParameterizedType(List::class.java, Reaction::class.java)
        val adapter = moshi.adapter<List<Reaction>>(type)
        val reactions = adapter.fromJson(REACTIONS)
        assertThat(reactions!!.size, isEqualTo(3))
        assertThat(reactions[0].shortname, isEqualTo(":hearts:"))
        assertThat(reactions[0].usernames.size, isEqualTo(1))
        assertThat(reactions[0].usernames[0], isEqualTo("leonardo.aramaki"))
        assertThat(reactions[1].shortname, isEqualTo(":vulcan:"))
        assertThat(reactions[1].usernames.size, isEqualTo(1))
        assertThat(reactions[1].usernames[0], isEqualTo("mr.spock"))
        assertThat(reactions[2].shortname, isEqualTo(":kotlin:"))
        assertThat(reactions[2].usernames.size, isEqualTo(2))
        assertThat(reactions[2].usernames[0], isEqualTo("andrey.breslav"))
        assertThat(reactions[2].usernames[1], isEqualTo("captain.underpants"))
    }
}