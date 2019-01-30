package ie.dublinmapper.repository.swordsexpress

import ie.dublinmapper.domain.model.SwordsExpressStop
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.swordsexpress.SwordsExpressStopJson
import ie.dublinmapper.util.Coordinate

object SwordsExpressStopMapper : Mapper<SwordsExpressStopJson, SwordsExpressStop> {

    override fun map(from: SwordsExpressStopJson): SwordsExpressStop {
        return SwordsExpressStop(
            id = from.id,
            name = from.name,
            coordinate = Coordinate(from.latitude, from.longitude),
            direction = from.direction
        )
    }

}
