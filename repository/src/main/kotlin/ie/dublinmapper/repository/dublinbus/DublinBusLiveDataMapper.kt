package ie.dublinmapper.repository.dublinbus

import ie.dublinmapper.domain.model.DublinBusLiveData
import ie.dublinmapper.domain.model.DueTime
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.TimeUtils
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

object DublinBusLiveDataMapper : Mapper<RtpiRealTimeBusInformationJson, DublinBusLiveData> {

    override fun map(from: RtpiRealTimeBusInformationJson): DublinBusLiveData {
        return DublinBusLiveData(
            mapDueTime(from.arrivalDateTime),
            Operator.DUBLIN_BUS,
            from.route,
            from.destination
        )
    }

    private fun mapDueTime(expectedArrivalDateTimestamp: String): List<DueTime> {
        val currentInstant = TimeUtils.now()
        val expectedInstant = TimeUtils.toInstant(expectedArrivalDateTimestamp)
        val minutes = TimeUtils.timeBetween(ChronoUnit.MINUTES, currentInstant, expectedInstant)
        return Collections.singletonList(DueTime(minutes, TimeUtils.toTime(expectedInstant)))
    }

}
