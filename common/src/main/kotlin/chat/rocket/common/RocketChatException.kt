package chat.rocket.common

open class RocketChatException(message: String, cause: Throwable? = null, private val url: String? = null) : RuntimeException(message, cause) {
    override fun toString(): String {
        return "${super.toString()}(url='$url')"
    }
}
