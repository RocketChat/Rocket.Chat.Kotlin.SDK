package chat.rocket.core.model

data class Setting<out T>(val id: String, val setting: Value<T>)