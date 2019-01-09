package ie.dublinmapper.util

import io.reactivex.Scheduler

data class Thread(
    val io: Scheduler,
    val ui: Scheduler
)
