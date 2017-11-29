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

/**
 * Login with username and password. On success this will also call [chat.rocket.core.TokenRepository].save(token)
 *
 * @param username Username
 * @param password Password
 * @param success ([Token]) lambda receiving the Authentication Token
 * @param error ([RocketChatException]) lambda indicating errors
 * @see Token
 * @see chat.rocket.core.TokenRepository
 *
 * @sample
 */
suspend fun RocketChatClient.login(username: String, password: String): Token {
    val body = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

    val url = requestUrl(restUrl, "login").build()

    val request = Request.Builder().url(url).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, Token::class.java)
    val result = handleRestCall<RestResult<Token>>(request, type).result()

    tokenRepository.save(result)

    return result
}

/**
 * Registers a new user within the server.
 *
 * Note, this doesn't authenticate the user. after a successful registration you still need to
 * call [login]
 *
 * @param email Email
 * @param name Name
 * @param username Username
 * @param password Password
 * @param success ([User]) lambda indicating success
 * @param error ([RocketChatException]) lambda indicating errors
 * @see User
 */
suspend fun RocketChatClient.signup(email: String,
                            name: String,
                            username: String,
                            password: String): User {
    val payload = UserPayload(email, name, password, username)
    val adapter = moshi.adapter(UserPayload::class.java)

    val paylodBody = adapter.toJson(payload)

    val contentType = MediaType.parse("application/json; charset=utf-8")
    val body = RequestBody.create(contentType, paylodBody)

    val url = requestUrl(restUrl, "users.register").build()
    val request = Request.Builder().url(url).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, User::class.java)
    return handleRestCall<RestResult<User>>(request, type).result()
}