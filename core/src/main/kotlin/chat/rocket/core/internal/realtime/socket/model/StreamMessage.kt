package chat.rocket.core.internal.realtime.socket.model

data class StreamMessage<out T>(val type: Type, val data: T)

sealed class Type {
    object Inserted : Type()
    object Updated : Type()
    object Removed : Type()
}
