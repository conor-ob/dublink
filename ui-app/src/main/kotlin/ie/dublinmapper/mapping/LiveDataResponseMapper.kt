package ie.dublinmapper.mapping

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import ie.dublinmapper.domain.usecase.LiveDataResponse
import ie.dublinmapper.domain.service.StringProvider
import ie.dublinmapper.model.DublinBikesLiveDataItem
import ie.dublinmapper.model.LiveDataItem
import io.rtpi.api.DublinBikesLiveData
import io.rtpi.api.TimedLiveData
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

class LiveDataResponseMapper(
    private val stringProvider: StringProvider
) : CustomConverter<LiveDataResponse, Group>() {

    //TODO write tests eg. empty live data returns empty group, no departures returns no "Departures" header item etc
    override fun convert(
        source: LiveDataResponse,
        destinationType: Type<out Group>,
        mappingContext: MappingContext?
    ) = Section(
        source.liveData.mapNotNull {
            when (it) {
                is TimedLiveData -> LiveDataItem(it)
                is DublinBikesLiveData -> DublinBikesLiveDataItem(it)
                else -> null
            }
        }
    )
}
