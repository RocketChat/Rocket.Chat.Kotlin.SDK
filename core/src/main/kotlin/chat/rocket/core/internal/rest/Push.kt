package chat.rocket.core.internal.rest

import chat.rocket.common.model.BaseResult
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.PushRegistrationPayload
import chat.rocket.core.internal.model.PushUnregistrationPayload
import chat.rocket.core.model.PushToken
import com.squareup.moshi.Types
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import okhttp3.RequestBody

suspend fun RocketChatClient.registerPushToken(token: String) = withContext(CommonPool) {
    val payload = PushRegistrationPayload(value = token)
    val adapter = moshi.adapter(PushRegistrationPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(RocketChatClient.CONTENT_TYPE_JSON, payloadBody)

    val httpUrl = requestUrl(restUrl, "push.token").build()
    val request = requestBuilderForAuthenticatedMethods(httpUrl).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, PushToken::class.java)

    handleRestCall<RestResult<PushToken>>(request, type).result()
}

suspend fun RocketChatClient.unregisterPushToken(token: String) = withContext(CommonPool) {
    val payload = PushUnregistrationPayload(token)
    val adapter = moshi.adapter(PushUnregistrationPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val body = RequestBody.create(RocketChatClient.CONTENT_TYPE_JSON, payloadBody)

    val httpUrl = requestUrl(restUrl, "push.token").build()
    val request = requestBuilderForAuthenticatedMethods(httpUrl).delete(body).build()

    handleRestCall<BaseResult>(request, BaseResult::class.java)
}
