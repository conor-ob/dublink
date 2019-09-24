package ie.dublinmapper.repository.buseireann.livedata

import ie.dublinmapper.domain.model.BusEireannLiveData
import ie.dublinmapper.domain.model.DueTime
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.Formatter
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.TimeUtils
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

object BusEireannLiveDataJsonToDomainMapper : CustomConverter<RtpiRealTimeBusInformationJson, BusEireannLiveData>() {

    override fun convert(
        source: RtpiRealTimeBusInformationJson,
        destinationType: Type<out BusEireannLiveData>,
        mappingContext: MappingContext
    ): BusEireannLiveData {
        return BusEireannLiveData(
            mapDueTime(source.arrivalDateTime!!),
            Operator.BUS_EIREANN,
            source.route!!,
            source.destination!!
        )
    }

    private fun mapDueTime(expectedArrivalDateTimestamp: String): List<DueTime> {
        val currentInstant = TimeUtils.now()
        val expectedInstant = TimeUtils.dateTimeStampToInstant(expectedArrivalDateTimestamp, Formatter.dateTime)
        val minutes = TimeUtils.timeBetween(ChronoUnit.MINUTES, currentInstant, expectedInstant)
        return Collections.singletonList(DueTime(minutes, TimeUtils.toTime(expectedInstant)))
    }

}
