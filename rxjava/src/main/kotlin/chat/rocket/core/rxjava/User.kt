package chat.rocket.core.rxjava

import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.rest.channelSubscriptions
import chat.rocket.core.internal.rest.dmSubscriptions
import chat.rocket.core.internal.rest.groupSubscriptions
import chat.rocket.core.internal.rest.me
import chat.rocket.core.model.Myself
import chat.rocket.core.model.Room
import io.reactivex.Single

fun RocketChatClient.me(): Single<Myself> =
    Single.create { emitter ->
        me(success = {
            emitter.onSuccess(it)
        }, error = {
            emitter.onError(it)
        })
    }

fun RocketChatClient.channelSubscriptions(offset: Long? = 0): Single<PaginatedResponse<Room>> =
        Single.create { emitter ->
            channelSubscriptions(offset, success = { rooms, total ->
                emitter.onSuccess(PaginatedResponse(rooms, total))
            }, error = {
                emitter.onError(it)
            })
        }

fun RocketChatClient.groupSubscriptions(offset: Long? = 0): Single<PaginatedResponse<Room>> =
        Single.create { emitter ->
            groupSubscriptions(offset, success = { rooms, total ->
                emitter.onSuccess(PaginatedResponse(rooms, total))
            }, error = {
                emitter.onError(it)
            })
        }

fun RocketChatClient.dmSubscriptions(offset: Long? = 0): Single<PaginatedResponse<Room>> =
        Single.create { emitter ->
            dmSubscriptions(offset, success = { rooms, total ->
                emitter.onSuccess(PaginatedResponse(rooms, total))
            }, error = {
                emitter.onError(it)
            })
        }