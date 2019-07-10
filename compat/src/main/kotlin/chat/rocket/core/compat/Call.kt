package chat.rocket.core.compat

import kotlinx.coroutines.Job

class Call(val job: Job) {
    fun cancel() {
        job.cancel()
    }
}