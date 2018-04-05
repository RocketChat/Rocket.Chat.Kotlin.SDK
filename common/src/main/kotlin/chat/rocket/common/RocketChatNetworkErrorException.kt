package chat.rocket.common

class RocketChatNetworkErrorException(message: String, cause: Throwable? = null, val url: String?) : RocketChatException(message, cause, url)