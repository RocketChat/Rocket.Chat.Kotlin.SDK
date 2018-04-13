package chat.rocket.core.internal.realtime.socket.model

sealed class State {
    class Created : State()
    class Waiting(val seconds: Int) : State()
    class Connecting : State()
    class Authenticating : State()
    class Connected : State()
    class Disconnecting : State()
    class Disconnected : State()
}