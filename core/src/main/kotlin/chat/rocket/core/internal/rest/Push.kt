package chat.rocket.core.internal.rest

import chat.rocket.common.model.BaseResult
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.RestResult
import chat.rocket.core.internal.model.PushRegistrationPayload
import chat.rocket.core.internal.model.PushUnregistrationPayload
import chat.rocket.core.model.PushToken
import com.squareup.moshi.Types
import okhttp3.MediaType
import okhttp3.RequestBody

suspend fun RocketChatClient.registerPushToken(token: String) {
    val payload = PushRegistrationPayload(value = token)
    val adapter = moshi.adapter(PushRegistrationPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val contentType = MediaType.parse("application/json; charset=utf-8")
    val body = RequestBody.create(contentType, payloadBody)

    val httpUrl = requestUrl(restUrl, "push.token").build()
    val request = requestBuilder(httpUrl).post(body).build()

    val type = Types.newParameterizedType(RestResult::class.java, PushToken::class.java)
    handleRestCall<RestResult<PushToken>>(request, type).result()
}

suspend fun RocketChatClient.unregisterPushToken(token: String) {
    val payload = PushUnregistrationPayload(token)
    val adapter = moshi.adapter(PushUnregistrationPayload::class.java)

    val payloadBody = adapter.toJson(payload)
    val contentType = MediaType.parse("application/json; charset=utf-8")
    val body = RequestBody.create(contentType, payloadBody)

    val httpUrl = requestUrl(restUrl, "push.token").build()
    val request = requestBuilder(httpUrl).delete(body).build()

    handleRestCall<BaseResult>(request, BaseResult::class.java)
}