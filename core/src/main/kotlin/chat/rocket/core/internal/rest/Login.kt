package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.common.model.Token
import chat.rocket.common.model.User
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.UsernameLoginPayload
import chat.rocket.core.internal.model.EmailLoginPayload
import chat.rocket.core.internal.model.UserPayload
import com.squareup.moshi.Types
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Login with username and password. On success this will also call [chat.rocket.core.TokenRepository].save(token)
 *
 * @param username Username
 * @param password Password of the user
 *
 * [Token] -> lambda receiving the Authentication Token
 * [RocketChatException] -> lambda indicating errors
 * @see Token
 * @see chat.rocket.core.TokenRepository
 *
 * @sample
 */
suspend fun RocketChatClient.login(username: String, password: String, pin: String? = null): Token = withContext(CommonPool) {
    val payload = UsernameLoginPayload(username, password, pin)
    val adapter = moshi.adapter(UsernameLoginPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "login").build()

    val request = Request.Builder().url(url).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, Token::class.java)
    val result = handleRestCall<RestResult<Token>>(request, type).result()

    tokenRepository.save(result)

    result
}

/**
 * Login with email and password. On success this will also call [chat.rocket.core.TokenRepository].save(token)
 *
 * @param user EmailId of the user
 * @param password Password of the user
 *
 * [Token] -> lambda receiving the Authentication Token
 * [RocketChatException] -> lambda indicating errors
 * @see Token
 * @see chat.rocket.core.TokenRepository
 *
 * @sample
 */
suspend fun RocketChatClient.loginWithEmail(email: String, password: String, pin: String? = null): Token = withContext(CommonPool) {
    val payload = EmailLoginPayload(email, password, pin)
    val adapter = moshi.adapter(EmailLoginPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "login").build()

    val request = Request.Builder().url(url).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, Token::class.java)
    val result = handleRestCall<RestResult<Token>>(request, type).result()

    tokenRepository.save(result)

    result
}

/**
 * Registers a new user within the server.
 *
 * Note, this doesn't authenticate the user. after a successful registration you still need to
 * call [login]
 *
 * @param email Email of the user
 * @param name Name
 * @param username Username
 * @param password Password of the user
 *
 * [Token] -> lambda receiving the Authentication Token
 * [RocketChatException] -> lambda indicating errors
 * @see User
 */
suspend fun RocketChatClient.signup(email: String,
                                    name: String,
                                    username: String,
                                    password: String): User = withContext(CommonPool) {
    val payload = UserPayload(null, email, name, password, username, null)
    val adapter = moshi.adapter(UserPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "users.register").build()
    val request = Request.Builder().url(url).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, User::class.java)
    handleRestCall<RestResult<User>>(request, type).result()
}