package chat.rocket.common

class RocketChatInvalidProtocolException(
    message: String,
    cause: Throwable? = null,
    url: String? = null
) : RocketChatException(message, cause, url)