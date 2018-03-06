package chat.rocket.core.internal.model

import se.ansman.kotshi.JsonSerializable
import java.util.*

@JsonSerializable
data class LdapLoginPayload(val ldap: Boolean = true,
                            val username: String,
                            val ldapPass: String,
                            val ldapOptions: Array<String> = emptyArray()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LdapLoginPayload

        if (ldap != other.ldap) return false
        if (username != other.username) return false
        if (ldapPass != other.ldapPass) return false
        if (!Arrays.equals(ldapOptions, other.ldapOptions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ldap.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + ldapPass.hashCode()
        result = 31 * result + Arrays.hashCode(ldapOptions)
        return result
    }
}