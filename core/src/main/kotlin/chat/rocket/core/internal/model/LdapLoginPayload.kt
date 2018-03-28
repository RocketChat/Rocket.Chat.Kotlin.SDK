package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class LdapLoginPayload(
    val ldap: Boolean = true,
    val username: String,
    val ldapPass: String,
    val ldapOptions: Array<String> = emptyArray()
)