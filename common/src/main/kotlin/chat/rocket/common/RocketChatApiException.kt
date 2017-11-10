package chat.rocket.common

class RocketChatApiException(
        val errorType: String,
        message: String,
        cause: Throwable? = null
) : RocketChatException(message, cause)