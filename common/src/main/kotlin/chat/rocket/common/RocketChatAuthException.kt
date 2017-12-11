package chat.rocket.common

class RocketChatAuthException(message: String, val error: String? = null) : RocketChatException(message)
