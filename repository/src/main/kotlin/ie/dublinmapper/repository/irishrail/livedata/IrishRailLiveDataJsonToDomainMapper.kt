package ie.dublinmapper.repository.irishrail.livedata

import ie.dublinmapper.domain.model.IrishRailLiveData
import ie.dublinmapper.domain.model.DueTime
import ie.dublinmapper.service.irishrail.IrishRailStationDataXml
import ie.dublinmapper.util.Formatter
import ie.dublinmapper.util.Operator
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type
import org.threeten.bp.LocalTime

object IrishRailLiveDataJsonToDomainMapper : CustomConverter<IrishRailStationDataXml, IrishRailLiveData>() {

    override fun convert(
        source: IrishRailStationDataXml,
        destinationType: Type<out IrishRailLiveData>,
        mappingContext: MappingContext
    ): IrishRailLiveData {
        val operator = mapOperator(source.trainType!!, source.trainCode!!)
        return IrishRailLiveData(
            dueTime = mapDueTime(source.expArrival!!, source.dueIn!!, source.queryTime!!),
            operator = operator,
            direction = source.direction!!,
            route = operator.fullName,
            destination = source.destination!!
        )
    }

    private fun mapDueTime(expectedArrivalTimestamp: String, dueInMinutes: String, queryTime: String): List<DueTime> {
        if (expectedArrivalTimestamp == "00:00") {
            val now = LocalTime.parse(queryTime, Formatter.hourMinuteSecond)
            val expectedTime = now.plusMinutes(dueInMinutes.toLong())
            return listOf(DueTime(dueInMinutes.toLong(), expectedTime))
        }
        val expectedTime = LocalTime.parse(expectedArrivalTimestamp, Formatter.hourMinute)
        return listOf(DueTime(dueInMinutes.toLong(), expectedTime))
    }

//    private fun mapDueTime(expectedArrivalTimestamp: String, dueInMinutes: String): List<DueTime> {
//        val currentInstant = TimeUtils.now()
//        if (expectedArrivalTimestamp == "00:00") {
//            val expectedInstant = currentInstant.plus(dueInMinutes.toLong(), ChronoUnit.MINUTES)
//            val minutes = TimeUtils.timeBetween(ChronoUnit.MINUTES, currentInstant, expectedInstant)
//            return Collections.singletonList(DueTime(minutes, TimeUtils.toTime(expectedInstant)))
//        }
//        //TODO bug where expectedArrivalTimestamp == "00:11" the next day - check Traindate field
//        val expectedInstant = TimeUtils.timestampToInstant(expectedArrivalTimestamp, Formatter.hourMinute)
//        val minutes = TimeUtils.timeBetween(ChronoUnit.MINUTES, currentInstant, expectedInstant)
//        return Collections.singletonList(DueTime(minutes, TimeUtils.toTime(expectedInstant)))
//    }

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
            "A", "B" -> Operator.INTERCITY
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
            "DART", "DART10" -> Operator.DART
            "DD/90", "ICR" -> Operator.INTERCITY
            else -> throw IllegalStateException("Unknown train type: $trainType, with code: $trainCode")
        }
    }

}
