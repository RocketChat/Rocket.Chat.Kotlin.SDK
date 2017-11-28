package chat.rocket.core.rxjava

import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.me
import chat.rocket.core.model.Myself
import io.reactivex.Single
import kotlinx.coroutines.experimental.rx2.rxSingle

fun RocketChatClient.me(): Single<Myself> =
    rxSingle {
        me()
    }