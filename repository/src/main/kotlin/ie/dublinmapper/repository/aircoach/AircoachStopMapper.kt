package ie.dublinmapper.repository.aircoach

import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.aircoach.AircoachStopJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator

object AircoachStopMapper : Mapper<AircoachStopJson, AircoachStop> {

    override fun map(from: AircoachStopJson): AircoachStop {
        return AircoachStop(
            from.id,
            from.name,
            Coordinate(from.stopLatitude, from.stopLongitude),
            Operator.aircoach()
        )
    }

}
