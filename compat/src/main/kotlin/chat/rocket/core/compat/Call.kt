package chat.rocket.core.compat

import kotlinx.coroutines.experimental.Job

class Call(val job: Job) {
    fun cancel() {
        job.cancel()
    }
}