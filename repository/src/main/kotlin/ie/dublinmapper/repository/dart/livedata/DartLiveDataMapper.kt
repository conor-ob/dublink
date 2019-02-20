package ie.dublinmapper.repository.dart.livedata

import ie.dublinmapper.domain.model.DartLiveData
import ie.dublinmapper.domain.model.DueTime
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.irishrail.IrishRailStationDataXml
import ie.dublinmapper.util.Formatter
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.TimeUtils
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

object DartLiveDataMapper : Mapper<IrishRailStationDataXml, DartLiveData> {

    override fun map(from: IrishRailStationDataXml): DartLiveData {
        return DartLiveData(
            mapDueTime(from.expArrival!!, from.dueIn!!),
            mapOperator(from.trainType!!, from.trainCode!!),
            from.destination!!,
            from.direction!!
        )
    }

    private fun mapDueTime(expectedArrivalTimestamp: String, dueInMinutes: String): List<DueTime> {
        val currentInstant = TimeUtils.now()
        if (expectedArrivalTimestamp == "00:00") {
            val expectedInstant = currentInstant.plus(dueInMinutes.toLong(), ChronoUnit.MINUTES)
            val minutes = TimeUtils.timeBetween(ChronoUnit.MINUTES, currentInstant, expectedInstant)
            return Collections.singletonList(DueTime(minutes, TimeUtils.toTime(expectedInstant)))
        }
        // bug where expectedArrivalTimestamp == "00:11" the next day
        val expectedInstant = TimeUtils.timestampToInstant(expectedArrivalTimestamp, Formatter.hourMinute)
        val minutes = TimeUtils.timeBetween(ChronoUnit.MINUTES, currentInstant, expectedInstant)
        return Collections.singletonList(DueTime(minutes, TimeUtils.toTime(expectedInstant)))
    }

    private fun mapOperator(trainType: String, trainCode: String): Operator {
        if (Operator.DART.shortName.equals(trainType, ignoreCase = true)) {
            return Operator.DART
        }
        return mapOperatorFromTrainCode(
            trainCode,
            trainType
        )
    }

    private fun mapOperatorFromTrainCode(trainCode: String, trainType: String): Operator {
        return when (trainCode.first().toString().toUpperCase()) {
            "E" -> Operator.DART
            "A" -> Operator.INTERCITY
            "D", "P" -> Operator.COMMUTER
            else -> mapOperatorFromTrainType(
                trainType,
                trainCode
            )
        }
    }

    private fun mapOperatorFromTrainType(trainType: String, trainCode: String): Operator {
        return when (trainType.toUpperCase()) {
            "ARROW" -> Operator.COMMUTER
            "DART" -> Operator.DART
            "DD/90", "ICR" -> Operator.INTERCITY
            else -> throw IllegalStateException("Unknown train type: $trainType, with code: $trainCode")
        }
    }

}
