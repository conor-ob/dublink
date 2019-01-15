package ie.dublinmapper.repository.dublinbikes

import ie.dublinmapper.domain.model.LiveData
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.jcdecaux.StationJson
import ie.dublinmapper.util.Operator

object DublinBikesLiveDataMapper : Mapper<StationJson, LiveData.DublinBikes> {

    override fun map(from: StationJson): LiveData.DublinBikes {
        return LiveData.DublinBikes(
            from.availableBikes,
            from.availableBikeStands,
            Operator.DUBLIN_BIKES
        )
    }

}
