package chat.rocket.common


class RocketChatApiException(error: Int, message: String, errorType: String) : RocketChatException(message)