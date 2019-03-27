package chat.rocket.core.internal.rest

import chat.rocket.common.model.Token
import chat.rocket.common.util.PlatformLogger
import chat.rocket.core.RocketChatClient
import chat.rocket.core.TokenRepository
import io.fabric8.mockwebserver.DefaultMockServer
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.hamcrest.CoreMatchers.`is` as isEqualTo

class CustomEmojiTest {

    private val EMOJI_CUSTOM_OK =
        """
{
  "emojis": [
    {
      "_id": "2cgzHwKP6Cq3iZCob",
      "name": "troll",
      "aliases": [],
      "extension": "jpg",
      "_updatedAt": "2017-01-27T19:52:20.427Z"
    },
    {
      "_id": "3TmCqTLBqFL4QLNPu",
      "name": "justdoit",
      "aliases": [],
      "extension": "png",
      "_updatedAt": "2016-09-19T17:56:37.325Z"
    },
    {
      "_id": "DiKTFLrWmspaaspeq",
      "name": "headdesk",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2016-09-08T21:56:14.887Z"
    },
    {
      "_id": "EukkRhwQaH3mnE26G",
      "name": "waiting",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-04-30T19:08:24.664Z"
    },
    {
      "_id": "F6BgkdaPnR9amsgZL",
      "name": "cool",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-04-30T19:06:06.044Z"
    },
    {
      "_id": "FMjXKhtouxCyPFoC2",
      "name": "computerrage",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-04-30T19:03:49.986Z"
    },
    {
      "_id": "FmSrrJgFsEw9u8r9y",
      "name": "wait",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-04-30T19:08:07.439Z"
    },
    {
      "_id": "HQgAjdME2me7iKn69",
      "name": "DancingBanana",
      "aliases": [
        "banana"
      ],
      "extension": "gif",
      "_updatedAt": "2016-09-05T16:34:20.446Z"
    },
    {
      "_id": "KYi4T2SE6LLZNsdSh",
      "name": "fury",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-04-30T19:09:59.083Z"
    },
    {
      "_id": "MKywCaNgYS2cAv2u9",
      "name": "doh",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-04-30T19:05:13.371Z"
    },
    {
      "_id": "NYjbDki9SxTYaaAkd",
      "name": "headbang",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2016-09-08T21:57:01.855Z"
    },
    {
      "_id": "TdTJhkxDRJ7eKrG7G",
      "name": "highfive",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-04-30T19:06:44.782Z"
    },
    {
      "_id": "X54WTtmCwGukzyPWn",
      "name": "notbad",
      "aliases": [],
      "extension": "jpg",
      "_updatedAt": "2016-09-21T20:52:03.241Z"
    },
    {
      "_id": "XraJefQsN8GqnAbTG",
      "name": "marioparty",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-04-30T19:02:53.985Z"
    },
    {
      "_id": "YMY2zgvJj27RHr2uh",
      "name": "fingerscrossed",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-04-30T19:05:35.324Z"
    },
    {
      "_id": "kcfzMXyJgMuX9wazh",
      "name": "fedora",
      "aliases": [],
      "extension": "png",
      "_updatedAt": "2017-07-14T00:37:13.049Z"
    },
    {
      "_id": "m645czF47suHvvNXZ",
      "name": "Ubuntu",
      "aliases": [
        "ubuntu"
      ],
      "extension": "png",
      "_updatedAt": "2016-09-06T14:53:42.744Z"
    },
    {
      "_id": "ptcktvY82WMxZRiPg",
      "name": "face-palm",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-01-27T15:22:54.134Z"
    },
    {
      "_id": "qgeckjv7oi3RQFEeE",
      "name": "yawning",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-04-30T19:07:36.057Z"
    },
    {
      "_id": "sbnEFAbiLRfsRdjWF",
      "name": "digital_ocean",
      "aliases": [],
      "extension": "png",
      "_updatedAt": "2016-09-09T00:51:04.190Z"
    },
    {
      "_id": "sqvgLuvCGY7ytAqmX",
      "name": "whew",
      "aliases": [
        "phew"
      ],
      "extension": "gif",
      "_updatedAt": "2016-09-21T16:06:41.776Z"
    },
    {
      "_id": "swMRjuzoX9vpKqoHp",
      "name": "explode",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2016-09-13T16:18:48.546Z"
    },
    {
      "_id": "tY4vy5vnkfR9Sijc7",
      "name": "docker",
      "aliases": [],
      "extension": "png",
      "_updatedAt": "2016-09-09T00:50:38.982Z"
    },
    {
      "_id": "uTpuqgnBRYZy6nXbc",
      "name": "ninja",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-04-30T19:08:59.937Z"
    },
    {
      "_id": "vm4MsDXRajCR4TCrc",
      "name": "rc",
      "aliases": [
        "rocketchat"
      ],
      "extension": "png",
      "_updatedAt": "2016-09-05T16:08:48.209Z"
    },
    {
      "_id": "z9wWP5RSwxPs3NuaB",
      "name": "lol",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-04-30T19:04:42.089Z"
    },
    {
      "_id": "zZS9kJeNfk24Dge8W",
      "name": "take_my_money",
      "aliases": [],
      "extension": "png",
      "_updatedAt": "2016-09-09T00:53:32.817Z"
    },
    {
      "_id": "QyvkpWFAQYrgEfDXm",
      "name": "ifood",
      "aliases": [],
      "extension": "png",
      "_updatedAt": "2017-08-17T14:31:12.409Z"
    },
    {
      "_id": "o8KdDTFRYpRApijeb",
      "name": "chiquinhosorvetes",
      "aliases": [
        "chiquinho"
      ],
      "extension": "png",
      "_updatedAt": "2017-09-04T18:06:07.960Z"
    },
    {
      "_id": "5ZptKQwzDmiH39KLM",
      "name": "nyan_rocket",
      "aliases": [],
      "extension": "png",
      "_updatedAt": "2017-09-06T19:38:35.803Z"
    },
    {
      "_id": "TFDSGfWqWH8JYoXuj",
      "name": "party_parrot",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-09-19T17:10:32.368Z"
    },
    {
      "_id": "Ato9hzRRkWCqHG3eC",
      "name": "illuminati",
      "aliases": [],
      "extension": "png",
      "_updatedAt": "2017-10-02T20:01:09.022Z"
    },
    {
      "_id": "2JwWCuwv8rBYFvZnP",
      "name": "totoro",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-10-02T20:24:31.695Z"
    },
    {
      "_id": "ivhBq9es4imh3N7Br",
      "name": "react_rocket",
      "aliases": [],
      "extension": "png",
      "_updatedAt": "2017-11-07T20:07:22.165Z"
    },
    {
      "_id": "BAYk3dTpx87wrWuiB",
      "name": "sad_parrot",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2017-12-05T10:15:58.863Z"
    },
    {
      "_id": "72uNmWQZHLaMxE3gi",
      "name": "vue",
      "aliases": [
        "vuejs"
      ],
      "extension": "png",
      "_updatedAt": "2017-12-05T22:54:15.445Z"
    },
    {
      "_id": "KYWrEtwpB5Gr2JSKK",
      "name": "kotlin",
      "aliases": [],
      "extension": "png",
      "_updatedAt": "2017-12-11T23:14:52.926Z"
    },
    {
      "_id": "scSbxNPzm9xWrNqCG",
      "name": "clapping",
      "aliases": [
        "clap"
      ],
      "extension": "gif",
      "_updatedAt": "2018-01-02T17:49:16.313Z"
    },
    {
      "_id": "E4mGw7jjHZzPrJgtF",
      "name": "dealwithitparrot",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2018-01-04T11:45:27.277Z"
    },
    {
      "_id": "kX2ZsAYZoJS6wd9YB",
      "name": "fidget_spinner",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2018-01-04T11:57:29.957Z"
    },
    {
      "_id": "ZZwt4cyKJgMfKNutP",
      "name": "s2london",
      "aliases": [
        "sean"
      ],
      "extension": "png",
      "_updatedAt": "2018-03-26T13:37:39.021Z"
    },
    {
      "_id": "Zn9wjRorNB4oQDdLX",
      "name": "chorrando",
      "aliases": [],
      "extension": "jpg",
      "_updatedAt": "2018-03-26T13:55:48.107Z"
    },
    {
      "_id": "Nz6hdc9XkssduZnCd",
      "name": "stormtrooper",
      "aliases": [],
      "extension": "png",
      "_updatedAt": "2018-04-26T19:11:09.190Z"
    },
    {
      "_id": "bYmN6svaiBcijzJj2",
      "name": "bb8",
      "aliases": [],
      "extension": "png",
      "_updatedAt": "2018-04-26T19:12:07.706Z"
    },
    {
      "_id": "FkjGSWZikNhWeaddc",
      "name": "lightsaber",
      "aliases": [],
      "extension": "png",
      "_updatedAt": "2018-04-26T19:12:51.055Z"
    },
    {
      "_id": "MfCewcTTdTaxEhrri",
      "name": "salt",
      "aliases": [
        "fn_salt"
      ],
      "extension": "gif",
      "_updatedAt": "2018-06-04T18:07:16.410Z"
    },
    {
      "_id": "Euh5ybr8BBRB6ebSo",
      "name": "gopher",
      "aliases": [],
      "extension": "gif",
      "_updatedAt": "2018-06-21T17:40:04.153Z"
    },
    {
      "_id": "b63AeZB5RCAKXvef4",
      "name": "kuririn",
      "aliases": [
        "saywhat"
      ],
      "extension": "png",
      "_updatedAt": "2018-06-21T20:05:12.076Z"
    }
  ],
  "success": true
}
"""
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
            tokenRepository = this@CustomEmojiTest.tokenProvider
            platformLogger = PlatformLogger.NoOpLogger()
        }

        Mockito.`when`(tokenProvider.get(sut.url)).thenReturn(authToken)
    }

    @Test
    fun `getCustomEmojis() should return list of custom emojis`() {
        mockServer.expect()
            .get()
            .withPath("/api/v1/emoji-custom")
            .andReturn(200, EMOJI_CUSTOM_OK)
            .once()

        runBlocking {
            val customEmojis = sut.getCustomEmojis()

            assertThat(customEmojis.size, isEqualTo(48))
            assertThat(customEmojis[0].name, isEqualTo("troll"))
            assertThat(customEmojis[1].name, isEqualTo("justdoit"))
            assertThat(customEmojis[2].name, isEqualTo("headdesk"))
            assertThat(customEmojis[47].name, isEqualTo("kuririn"))
            assertThat(customEmojis[0].extension, isEqualTo("jpg"))
            assertThat(customEmojis[1].extension, isEqualTo("png"))
            assertThat(customEmojis[14].extension, isEqualTo("gif"))
            assertThat(customEmojis[47].extension, isEqualTo("png"))
        }
    }
}
