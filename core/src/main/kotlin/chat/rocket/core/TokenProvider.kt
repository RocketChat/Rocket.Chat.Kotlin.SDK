package chat.rocket.core

import chat.rocket.common.model.Token

interface TokenProvider {
    fun save(token: Token)

    fun get(): Token?
}