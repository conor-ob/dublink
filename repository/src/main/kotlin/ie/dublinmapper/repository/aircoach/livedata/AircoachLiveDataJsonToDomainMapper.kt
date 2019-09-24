package ie.dublinmapper.repository.aircoach.livedata

import ie.dublinmapper.domain.model.AircoachLiveData
import ie.dublinmapper.domain.model.DueTime
import ie.dublinmapper.service.aircoach.EtaJson
import ie.dublinmapper.service.aircoach.ServiceJson
import ie.dublinmapper.service.aircoach.TimestampJson
import ie.dublinmapper.util.Formatter
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.TimeUtils
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

object AircoachLiveDataJsonToDomainMapper : CustomConverter<ServiceJson, AircoachLiveData>() {

    override fun convert(
        source: ServiceJson,
        destinationType: Type<out AircoachLiveData>,
        mappingContext: MappingContext
    ): AircoachLiveData {
        return AircoachLiveData(
            mapDueTime(source.eta, source.time.arrive),
            Operator.AIRCOACH,
            source.route,
            source.arrival
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
