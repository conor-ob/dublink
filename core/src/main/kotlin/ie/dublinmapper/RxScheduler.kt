package ie.dublinmapper

import io.reactivex.Scheduler

data class RxScheduler(
    val io: Scheduler,
    val ui: Scheduler
)
