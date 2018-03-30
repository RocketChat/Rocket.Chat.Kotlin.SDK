package chat.rocket.core

import chat.rocket.common.model.Token

/**
 *
 */
interface TokenRepository {
    fun save(url: String, token: Token)

    fun get(url: String): Token?
}