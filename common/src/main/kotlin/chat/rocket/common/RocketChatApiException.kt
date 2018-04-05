package chat.rocket.common

class RocketChatApiException(
        val errorType: String,
        message: String,
        cause: Throwable? = null,
        url: String? = null
) : RocketChatException(message, cause, url)