package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.common.model.Token
import chat.rocket.common.model.User
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.UserPayload
import com.squareup.moshi.Types
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody

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

fun RocketChatClient.signup(email: String,
                            name: String,
                            username: String,
                            password: String,
                            success: (User) -> Unit,
                            error: (RocketChatException) -> Unit) {
    val payload = UserPayload(email, name, password, username)
    val adapter = moshi.adapter(UserPayload::class.java)

    val paylodBody = adapter.toJson(payload)

    val contentType = MediaType.parse("application/json; charset=utf-8")
    val body = RequestBody.create(contentType, paylodBody)

    val url = requestUrl(restUrl, "users.register").build()
    val request = Request.Builder().url(url).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, User::class.java)
    handleRestCall<RestResult<User>>(request, type, {
        success.invoke(it.result())
    }, error)
}