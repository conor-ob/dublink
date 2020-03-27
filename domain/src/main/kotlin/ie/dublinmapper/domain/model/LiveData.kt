package ie.dublinmapper.domain.model

import io.rtpi.api.TimedLiveData
import java.time.Duration

fun TimedLiveData.isLate(): Boolean {
    return Duration.between(liveTime.scheduledDateTime, liveTime.currentDateTime).toMinutes().toInt() > 1
            && Duration.between(liveTime.currentDateTime, liveTime.scheduledDateTime).toMinutes().toInt() <= 15
}

fun TimedLiveData.isDelayed(): Boolean {
    return Duration.between(liveTime.scheduledDateTime, liveTime.expectedDateTime).toMinutes().toInt() > 1
            && Duration.between(liveTime.currentDateTime, liveTime.scheduledDateTime).toMinutes().toInt() <= 15
}

fun TimedLiveData.isOnTime(): Boolean {
    return Duration.between(liveTime.scheduledDateTime, liveTime.expectedDateTime).toMinutes().toInt() <= 1
            && Duration.between(liveTime.currentDateTime, liveTime.scheduledDateTime).toMinutes().toInt() <= 15
}

fun TimedLiveData.minutesLate(): Int {
    return Duration.between(liveTime.scheduledDateTime, liveTime.currentDateTime).toMinutes().toInt()
}

fun TimedLiveData.minutesDelayed(): Int {
    return Duration.between(liveTime.scheduledDateTime, liveTime.expectedDateTime).toMinutes().toInt()
}
