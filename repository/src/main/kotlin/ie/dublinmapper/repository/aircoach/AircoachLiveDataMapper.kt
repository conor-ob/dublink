package ie.dublinmapper.repository.aircoach

import ie.dublinmapper.domain.model.AircoachLiveData
import ie.dublinmapper.domain.model.DueTime
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.aircoach.EtaJson
import ie.dublinmapper.service.aircoach.ServiceJson
import ie.dublinmapper.service.aircoach.TimestampJson
import ie.dublinmapper.util.Formatter
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.TimeUtils
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

object AircoachLiveDataMapper : Mapper<ServiceJson, AircoachLiveData> {

    override fun map(from: ServiceJson): AircoachLiveData {
        return AircoachLiveData(
            mapDueTime(from.eta, from.time.arrive),
            Operator.AIRCOACH,
            from.route,
            from.arrival
        )
    }

    private fun mapDueTime(expected: EtaJson?, scheduled: TimestampJson): List<DueTime> {
        val currentInstant = TimeUtils.now()
        if (expected == null) {
            val scheduledInstant = TimeUtils.dateTimeStampToInstant(scheduled.dateTime, Formatter.dateTimeAlt)
            val minutes = TimeUtils.timeBetween(ChronoUnit.MINUTES, currentInstant, scheduledInstant)
            return Collections.singletonList(DueTime(minutes, TimeUtils.toTime(scheduledInstant)))
        }
        val expectedInstant = TimeUtils.dateTimeStampToInstant(expected.etaArrive.dateTime, Formatter.dateTimeAlt)
        val minutes = TimeUtils.timeBetween(ChronoUnit.MINUTES, currentInstant, expectedInstant)
        return Collections.singletonList(DueTime(minutes, TimeUtils.toTime(expectedInstant)))
    }

}
