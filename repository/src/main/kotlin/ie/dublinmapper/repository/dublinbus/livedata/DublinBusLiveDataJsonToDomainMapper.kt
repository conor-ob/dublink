package ie.dublinmapper.repository.dublinbus.livedata

import ie.dublinmapper.domain.model.DublinBusLiveData
import ie.dublinmapper.domain.model.DueTime
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.Formatter
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.TimeUtils
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type
import org.threeten.bp.LocalTime

object DublinBusLiveDataJsonToDomainMapper : CustomConverter<RtpiRealTimeBusInformationJson, DublinBusLiveData>() {

    override fun convert(
        source: RtpiRealTimeBusInformationJson,
        destinationType: Type<out DublinBusLiveData>,
        mappingContext: MappingContext
    ): DublinBusLiveData {
        return DublinBusLiveData(
            mapDueTime(source.arrivalDateTime!!, source.dueTime!!),
            Operator.parse(source.operator!!),
            source.route!!,
            source.destination!!
        )
    }

    private fun mapDueTime(expectedArrivalDateTimestamp: String, minutes: String): List<DueTime> {
        val expectedInstant = TimeUtils.dateTimeStampToInstant(expectedArrivalDateTimestamp, Formatter.isoDateTime)
        return listOf(DueTime(minutes.toLong(), LocalTime.now())) //TODO
    }

}
