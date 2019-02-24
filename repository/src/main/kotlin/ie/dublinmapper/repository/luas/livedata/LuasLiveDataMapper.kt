package ie.dublinmapper.repository.luas.livedata

import ie.dublinmapper.domain.model.DueTime
import ie.dublinmapper.domain.model.LuasLiveData
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.Formatter
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.TimeUtils
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

object LuasLiveDataMapper : Mapper<RtpiRealTimeBusInformationJson, LuasLiveData> {

    override fun map(from: RtpiRealTimeBusInformationJson): LuasLiveData {
        return LuasLiveData(
            mapDueTime(from.arrivalDateTime),
            Operator.LUAS,
            from.route,
            mapDestination(from)
        )
    }

    private fun mapDestination(from: RtpiRealTimeBusInformationJson): String {
        return from.destination.replace("LUAS", "").trim()
    }

    private fun mapDueTime(expectedArrivalDateTimestamp: String): List<DueTime> {
        val currentInstant = TimeUtils.now()
        val expectedInstant = TimeUtils.dateTimeStampToInstant(expectedArrivalDateTimestamp, Formatter.dateTime)
        val minutes = TimeUtils.timeBetween(ChronoUnit.MINUTES, currentInstant, expectedInstant)
        return Collections.singletonList(DueTime(minutes, TimeUtils.toTime(expectedInstant)))
    }

}
