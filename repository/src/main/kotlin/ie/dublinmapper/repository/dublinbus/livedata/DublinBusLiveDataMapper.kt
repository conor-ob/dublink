package ie.dublinmapper.repository.dublinbus.livedata

import ie.dublinmapper.domain.model.DublinBusLiveData
import ie.dublinmapper.domain.model.DueTime
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.Formatter
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.TimeUtils
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

object DublinBusLiveDataMapper : Mapper<RtpiRealTimeBusInformationJson, DublinBusLiveData> {

    override fun map(from: RtpiRealTimeBusInformationJson): DublinBusLiveData {
        return DublinBusLiveData(
            mapDueTime(from.arrivalDateTime!!, from.dueTime!!),
            Operator.DUBLIN_BUS,
            from.route!!,
            from.destination!!
        )
    }

    private fun mapDueTime(expectedArrivalDateTimestamp: String, minutes: String): List<DueTime> {
        val expectedInstant = TimeUtils.dateTimeStampToInstant(expectedArrivalDateTimestamp, Formatter.isoDateTime)
        return listOf(DueTime(minutes.toLong(), LocalTime.now())) //TODO
    }

}
