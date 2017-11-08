package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.common.model.Token
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import com.squareup.moshi.Types
import okhttp3.FormBody
import okhttp3.Request

fun RocketChatClient.login(username: String, password: String, success: (Token) -> Unit,
                           error: (RocketChatException) -> Unit) {
    val body = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

    val url = requestUrl(restUrl, "login").build()

    val request = Request.Builder().url(url).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, Token::class.java)
    handleRestCall<RestResult<Token>>(request, type, {
        tokenProvider.save(it.result())
        success.invoke(it.result())
    }, error)
}