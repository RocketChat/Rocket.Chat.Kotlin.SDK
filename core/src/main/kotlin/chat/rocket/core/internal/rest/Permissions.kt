package chat.rocket.core.internal.rest

import chat.rocket.core.RocketChatClient
import chat.rocket.core.model.Permission
import com.squareup.moshi.Types
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext

/**
 * Request all permissions associated to each role at current server.
 *
 * @return A list containing all permission types and their associated roles.
 */
suspend fun RocketChatClient.permissions(): List<Permission> = withContext(CommonPool) {
    val url = requestUrl(restUrl, "permissions").build()
    val request = requestBuilderForAuthenticatedMethods(url).get().build()

    val type = Types.newParameterizedType(List::class.java, Permission::class.java)
    return@withContext handleRestCall<List<Permission>>(request, type)
}