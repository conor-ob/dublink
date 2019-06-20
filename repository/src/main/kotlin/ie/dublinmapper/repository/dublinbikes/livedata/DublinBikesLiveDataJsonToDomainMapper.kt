package ie.dublinmapper.repository.dublinbikes.livedata

import ie.dublinmapper.domain.model.DublinBikesLiveData
import ie.dublinmapper.service.jcdecaux.StationJson
import ie.dublinmapper.util.Operator
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object DublinBikesLiveDataJsonToDomainMapper : CustomConverter<StationJson, DublinBikesLiveData>() {

    override fun convert(
        source: StationJson,
        destinationType: Type<out DublinBikesLiveData>,
        mappingContext: MappingContext
    ): DublinBikesLiveData {
        return DublinBikesLiveData(
            operator = Operator.DUBLIN_BIKES,
            bikes = source.availableBikes,
            docks = source.availableBikeStands
        )
    }

}
