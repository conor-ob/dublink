package ie.dublinmapper.repository.buseireann.livedata

import ie.dublinmapper.domain.model.BusEireannLiveData
import ie.dublinmapper.domain.model.DueTime
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.Formatter
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.TimeUtils
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

object BusEireannLiveDataMapper : Mapper<RtpiRealTimeBusInformationJson, BusEireannLiveData> {

    override fun map(from: RtpiRealTimeBusInformationJson): BusEireannLiveData {
        return BusEireannLiveData(
            mapDueTime(from.arrivalDateTime!!),
            Operator.BUS_EIREANN,
            from.route!!,
            from.destination!!
        )
    }

    private fun mapDueTime(expectedArrivalDateTimestamp: String): List<DueTime> {
        val currentInstant = TimeUtils.now()
        val expectedInstant = TimeUtils.dateTimeStampToInstant(expectedArrivalDateTimestamp, Formatter.dateTime)
        val minutes = TimeUtils.timeBetween(ChronoUnit.MINUTES, currentInstant, expectedInstant)
        return Collections.singletonList(DueTime(minutes, TimeUtils.toTime(expectedInstant)))
    }

}
