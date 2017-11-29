package chat.rocket.core.rxjava

import chat.rocket.common.model.Token
import chat.rocket.common.model.User
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.login
import chat.rocket.core.internal.rest.signup
import io.reactivex.Single
import kotlinx.coroutines.experimental.rx2.rxSingle

/**
 * Login with username and password. On success this will also call [chat.rocket.core.TokenRepository].save(token)
 *
 * @param username Username
 * @param password Password
 * @return [Single]<[Token]>
 * @see Token
 * @see chat.rocket.core.TokenRepository
 *
 * @sample
 */
fun RocketChatClient.login(username: String, password: String): Single<Token> =
        rxSingle {
            login(username, password)
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
 * @return [Single]<[User]>
 * @see User
 */
fun RocketChatClient.signup(email: String,
                            name: String,
                            username: String,
                            password: String): Single<User> =
        rxSingle {
            signup(email, name, username, password)
        }