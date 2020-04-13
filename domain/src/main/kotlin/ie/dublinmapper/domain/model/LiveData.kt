package ie.dublinmapper.domain.model

import io.rtpi.api.PredictionLiveData
import java.time.Duration

fun PredictionLiveData.isLate(): Boolean {
    return Duration.between(prediction.scheduledDateTime, prediction.currentDateTime).toMinutes().toInt() > 1 &&
            Duration.between(prediction.currentDateTime, prediction.scheduledDateTime).toMinutes().toInt() <= 15
}

fun PredictionLiveData.isDelayed(): Boolean {
    return Duration.between(prediction.scheduledDateTime, prediction.expectedDateTime).toMinutes().toInt() > 1 &&
            Duration.between(prediction.currentDateTime, prediction.scheduledDateTime).toMinutes().toInt() <= 15
}

fun PredictionLiveData.isOnTime(): Boolean {
    return Duration.between(prediction.scheduledDateTime, prediction.expectedDateTime).toMinutes().toInt() <= 1 &&
            Duration.between(prediction.currentDateTime, prediction.scheduledDateTime).toMinutes().toInt() <= 15
}

fun PredictionLiveData.minutesLate(): Int {
    return Duration.between(prediction.scheduledDateTime, prediction.currentDateTime).toMinutes().toInt()
}

fun PredictionLiveData.minutesDelayed(): Int {
    return Duration.between(prediction.scheduledDateTime, prediction.expectedDateTime).toMinutes().toInt()
}
