package chat.rocket.common

class RocketChatInvalidResponseException(message: String, cause: Throwable? = null, url: String? = null) : RocketChatException(message, cause, url)