package chat.rocket.core.internal.rest

import chat.rocket.common.RocketChatException
import chat.rocket.common.model.BaseResult
import chat.rocket.common.model.Token
import chat.rocket.common.model.User
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.CasLoginPayload
import chat.rocket.core.internal.model.OauthLoginPayload
import chat.rocket.core.internal.model.UsernameLoginPayload
import chat.rocket.core.internal.model.EmailLoginPayload
import chat.rocket.core.internal.model.LdapLoginPayload
import chat.rocket.core.internal.model.SamlLoginPayload
import chat.rocket.core.internal.model.SignUpPayload
import chat.rocket.core.internal.model.ForgotPasswordPayload
import chat.rocket.core.internal.model.CasData
import chat.rocket.core.internal.model.OauthData
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody

/**
 * Login with username and password.
 * On success this will also call [chat.rocket.core.TokenRepository].save(token).
 *
 * @param username Username of the user.
 * @param password Password of the user.
 *
 * @return [Token]
 * @throws [RocketChatException] on errors.
 * @see [Token]
 * @see [chat.rocket.core.TokenRepository]
 */
suspend fun RocketChatClient.login(username: String, password: String, pin: String? = null): Token =
    withContext(Dispatchers.IO) {
        val payload = UsernameLoginPayload(username, password, pin)
        val adapter = moshi.adapter(UsernameLoginPayload::class.java)

        val payloadBody = adapter.toJson(payload)
        val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

        val httpUrl = requestUrl(restUrl, "login").build()
        val request = requestBuilder(httpUrl).post(body).build()

        val type = Types.newParameterizedType(RestResult::class.java, Token::class.java)
        val result = handleRestCall<RestResult<Token>>(request, type).result()

        tokenRepository.save(url, result)

        result
    }

/**
 * Login with email and password.
 * On success this will also call [chat.rocket.core.TokenRepository].save(token)
 *
 * @param email Email of the user.
 * @param password Password of the user.
 *
 * @return [Token]
 * @throws [RocketChatException] on errors.
 * @see [Token]
 * @see [chat.rocket.core.TokenRepository]
 */
suspend fun RocketChatClient.loginWithEmail(email: String, password: String, pin: String? = null): Token = withContext(Dispatchers.IO) {
    val payload = EmailLoginPayload(email, password, pin)
    val adapter = moshi.adapter(EmailLoginPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val httpUrl = requestUrl(restUrl, "login").build()
    val request = requestBuilder(httpUrl).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, Token::class.java)
    val result = handleRestCall<RestResult<Token>>(request, type).result()

    tokenRepository.save(url, result)

    result
}

/**
 * Login with username and password through LDAP.
 * On success this will also call [chat.rocket.core.TokenRepository].save(token)
 *
 * @param username Username of the user.
 * @param password Password of the user.
 *
 * @return [Token]
 * @throws [RocketChatException] on errors.
 * @see [Token]
 * @see [chat.rocket.core.TokenRepository]
 */
suspend fun RocketChatClient.loginWithLdap(username: String, password: String): Token = withContext(Dispatchers.IO) {
    val payload = LdapLoginPayload(true, username, password)
    val adapter = moshi.adapter(LdapLoginPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val httpUrl = requestUrl(restUrl, "login").build()
    val request = requestBuilder(httpUrl).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, Token::class.java)
    val result = handleRestCall<RestResult<Token>>(request, type).result()

    tokenRepository.save(url, result)

    result
}

/**
 * Login through CAS protocol.
 * On success this will also call [chat.rocket.core.TokenRepository].save(token)
 *
 * @param casCredential The CAS credential to authenticate with.
 *
 * @return [Token]
 * @throws [RocketChatException] on errors.
 * @see [Token]
 * @see [chat.rocket.core.TokenRepository]
 */
suspend fun RocketChatClient.loginWithCas(casCredential: String): Token = withContext(Dispatchers.IO) {
    val payload = CasLoginPayload(CasData(casCredential))
    val adapter = moshi.adapter(CasLoginPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val httpUrl = requestUrl(restUrl, "login").build()
    val request = requestBuilder(httpUrl).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, Token::class.java)
    val result = handleRestCall<RestResult<Token>>(request, type).result()

    tokenRepository.save(url, result)

    result
}

/**
 * Login through SAML.
 * On success this will also call [chat.rocket.core.TokenRepository].save(token)
 *
 * @param samlCredential The SAML credential to authenticate with.
 *
 * @return [Token]
 * @throws [RocketChatException] on errors.
 * @see [Token]
 * @see [chat.rocket.core.TokenRepository]
 */
suspend fun RocketChatClient.loginWithSaml(samlCredential: String): Token = withContext(Dispatchers.IO) {
    val payload = SamlLoginPayload(true, samlCredential)
    val adapter = moshi.adapter(SamlLoginPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val httpUrl = requestUrl(restUrl, "login").build()
    val request = requestBuilder(httpUrl).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, Token::class.java)
    val result = handleRestCall<RestResult<Token>>(request, type).result()

    tokenRepository.save(url, result)

    result
}

/**
 * Login through OAuth.
 * On success this will also call [chat.rocket.core.TokenRepository].save(token)
 *
 * @param credentialToken The OAuth credential token to authenticate with.
 * @param credentialSecret The OAuth credential secret to authenticate with.
 *
 * @return [Token]
 * @throws [RocketChatException] on errors.
 * @see [Token]
 * @see [chat.rocket.core.TokenRepository]
 */
suspend fun RocketChatClient.loginWithOauth(credentialToken: String, credentialSecret: String): Token = withContext(Dispatchers.IO) {
    val payload = OauthLoginPayload(OauthData(credentialToken, credentialSecret))
    val adapter = moshi.adapter(OauthLoginPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val httpUrl = requestUrl(restUrl, "login").build()
    val request = requestBuilder(httpUrl).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, Token::class.java)
    val result = handleRestCall<RestResult<Token>>(request, type).result()

    tokenRepository.save(url, result)

    result
}

/**
 * Registers a new user within the server.
 * Note, this doesn't authenticate the user.
 * After a successful registration you still need to call [login].
 *
 * @param email Email of the user.
 * @param name Name of the user.
 * @param username Username of the user.
 * @param password Password of the user.
 *
 * @return [Token]
 * @throws [RocketChatException] on errors.
 * @see [User]
 */
suspend fun RocketChatClient.signup(
    email: String,
    name: String,
    username: String,
    password: String
): User = withContext(Dispatchers.IO) {
    val payload = SignUpPayload(username, email, password, name)
    val adapter = moshi.adapter(SignUpPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "users.register").build()
    val request = requestBuilder(url).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, User::class.java)
    handleRestCall<RestResult<User>>(request, type).result()
}

/**
 * Dispatches an email with a link to the user be able to reset its password.
 *
 * @param email Email of the user.
 * @throws [RocketChatException] if the user was not found.
 */
suspend fun RocketChatClient.forgotPassword(email: String): Boolean {
    val payload = ForgotPasswordPayload(email)
    val adapter = moshi.adapter(ForgotPasswordPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(MEDIA_TYPE_JSON, payloadBody)

    val url = requestUrl(restUrl, "users.forgotPassword").build()
    val request = requestBuilder(url).post(body).build()

    return handleRestCall<BaseResult>(request, BaseResult::class.java).success
}