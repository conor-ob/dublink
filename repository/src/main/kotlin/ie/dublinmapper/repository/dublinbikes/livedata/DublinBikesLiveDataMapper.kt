package ie.dublinmapper.repository.dublinbikes.livedata

import ie.dublinmapper.domain.model.DublinBikesLiveData
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.jcdecaux.StationJson
import ie.dublinmapper.util.Operator

object DublinBikesLiveDataMapper : Mapper<StationJson, DublinBikesLiveData> {

    override fun map(from: StationJson): DublinBikesLiveData {
        return DublinBikesLiveData(
            operator = Operator.DUBLIN_BIKES,
            bikes = from.availableBikes,
            docks = from.availableBikeStands
        )
    }

}
