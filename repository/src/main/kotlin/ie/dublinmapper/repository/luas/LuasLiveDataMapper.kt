package ie.dublinmapper.repository.luas

import ie.dublinmapper.domain.model.DueTime
import ie.dublinmapper.domain.model.LiveData
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.TimeUtils
import org.threeten.bp.temporal.ChronoUnit

object LuasLiveDataMapper : Mapper<RtpiRealTimeBusInformationJson, LiveData.Luas> {

    override fun map(from: RtpiRealTimeBusInformationJson): LiveData.Luas {
        return LiveData.Luas(
            mapDueTime(from.arrivalDateTime),
            Operator.LUAS,
            from.route,
            from.destination

        )
    }

    private fun mapDueTime(expectedArrivalDateTimestamp: String): DueTime {
        val currentInstant = TimeUtils.now()
        val expectedInstant = TimeUtils.toInstant(expectedArrivalDateTimestamp)
        val minutes = TimeUtils.timeBetween(ChronoUnit.MINUTES, currentInstant, expectedInstant)
        return DueTime(minutes, TimeUtils.toTime(expectedInstant))
    }

}
