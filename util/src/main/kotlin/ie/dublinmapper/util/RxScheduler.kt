package ie.dublinmapper.util

import io.reactivex.Scheduler

data class RxScheduler(
    val io: Scheduler,
    val ui: Scheduler
)
