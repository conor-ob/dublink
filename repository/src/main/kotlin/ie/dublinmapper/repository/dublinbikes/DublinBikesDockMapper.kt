package ie.dublinmapper.repository.dublinbikes

import ie.dublinmapper.domain.model.DublinBikesDock
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.jcdecaux.StationJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator

object DublinBikesDockMapper : Mapper<StationJson, DublinBikesDock> {

    override fun map(from: StationJson): DublinBikesDock {
        return DublinBikesDock(
            from.number.toString(),
            from.address,
            Coordinate(from.position.lat, from.position.lng),
            Operator.dublinBikes(),
            from.availableBikes
        )
    }

}
