package ie.dublinmapper.repository.luas.livedata

import ie.dublinmapper.domain.model.DueTime
import ie.dublinmapper.domain.model.LuasLiveData
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.Formatter
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.TimeUtils
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

object LuasLiveDataJsonToDomainMapper : CustomConverter<RtpiRealTimeBusInformationJson, LuasLiveData>() {

    override fun convert(
        source: RtpiRealTimeBusInformationJson,
        destinationType: Type<out LuasLiveData>,
        mappingContext: MappingContext
    ): LuasLiveData {
        return LuasLiveData(
            mapDueTime(source.arrivalDateTime!!),
            Operator.LUAS,
            source.route!!,
            mapDestination(source),
            source.direction!!
        )
    }

    private fun mapDestination(from: RtpiRealTimeBusInformationJson): String {
        return from.destination!!.replace("LUAS", "").trim()
    }

    private fun mapDueTime(expectedArrivalDateTimestamp: String): List<DueTime> {
        val currentInstant = TimeUtils.now()
        val expectedInstant = TimeUtils.dateTimeStampToInstant(expectedArrivalDateTimestamp, Formatter.dateTime)
        val minutes = TimeUtils.timeBetween(ChronoUnit.MINUTES, currentInstant, expectedInstant)
        return Collections.singletonList(DueTime(minutes, TimeUtils.toTime(expectedInstant)))
    }

}
