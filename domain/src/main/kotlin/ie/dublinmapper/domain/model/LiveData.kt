package ie.dublinmapper.domain.model

import io.rtpi.api.TimedLiveData
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime

fun TimedLiveData.isLate(): Boolean {
    val currentDateTime = ZonedDateTime.parse(liveTime.currentTimestamp)
    val scheduledDateTime = ZonedDateTime.parse(liveTime.scheduledTimestamp)
    return Duration.between(scheduledDateTime, currentDateTime).toMinutes().toInt() > 1
            && Duration.between(currentDateTime, scheduledDateTime).toMinutes().toInt() <= 15
}

fun TimedLiveData.isDelayed(): Boolean {
    val currentDateTime = ZonedDateTime.parse(liveTime.currentTimestamp)
    val scheduledDateTime = ZonedDateTime.parse(liveTime.scheduledTimestamp)
    val expectedDateTime = ZonedDateTime.parse(liveTime.expectedTimestamp)
    return Duration.between(scheduledDateTime, expectedDateTime).toMinutes().toInt() > 1
            && Duration.between(currentDateTime, scheduledDateTime).toMinutes().toInt() <= 15
}

fun TimedLiveData.isOnTime(): Boolean {
    val currentDateTime = ZonedDateTime.parse(liveTime.currentTimestamp)
    val scheduledDateTime = ZonedDateTime.parse(liveTime.scheduledTimestamp)
    val expectedDateTime = ZonedDateTime.parse(liveTime.expectedTimestamp)
    return Duration.between(scheduledDateTime, expectedDateTime).toMinutes().toInt() <= 1
            && Duration.between(currentDateTime, scheduledDateTime).toMinutes().toInt() <= 15
}

fun TimedLiveData.minutesLate(): Int {
    val currentDateTime = ZonedDateTime.parse(liveTime.currentTimestamp)
    val scheduledDateTime = ZonedDateTime.parse(liveTime.scheduledTimestamp)
    return Duration.between(scheduledDateTime, currentDateTime).toMinutes().toInt()
}

fun TimedLiveData.minutesDelayed(): Int {
    val scheduledDateTime = ZonedDateTime.parse(liveTime.scheduledTimestamp)
    val expectedDateTime = ZonedDateTime.parse(liveTime.expectedTimestamp)
    return Duration.between(scheduledDateTime, expectedDateTime).toMinutes().toInt()
}
