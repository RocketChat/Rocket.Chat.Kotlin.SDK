package chat.rocket.core.compat

import chat.rocket.common.model.ServerInfo
import chat.rocket.core.RocketChatClient
import chat.rocket.core.compat.internal.callback
import chat.rocket.core.internal.rest.serverInfo
import kotlinx.coroutines.experimental.CommonPool

fun RocketChatClient.serverInfo(future: Callback<ServerInfo>): Call =
        callback(CommonPool, future) {
            serverInfo()
        }