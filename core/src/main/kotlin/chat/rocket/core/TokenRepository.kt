package chat.rocket.core

import chat.rocket.common.model.Token

/**
 *
 */
interface TokenRepository {
    fun save(token: Token)

    fun get(): Token?
}