package ie.dublinmapper.repository.aircoach

import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.aircoach.AircoachStopJson
import ie.dublinmapper.service.aircoach.AircoachStopServiceJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator

object AircoachStopMapper : Mapper<AircoachStopJson, AircoachStop> {

    override fun map(from: AircoachStopJson): AircoachStop {
        return AircoachStop(
            from.id,
            from.name,
            Coordinate(from.stopLatitude, from.stopLongitude),
            Operator.aircoach(),
            mapOperatorsToRoutes(from.services)
        )
    }

    private fun mapOperatorsToRoutes(services: List<AircoachStopServiceJson>): Map<Operator, Set<String>> {
        val routes = mutableSetOf<String>()
        for (service in services) {
            routes.add(service.route)
        }
        return mapOf(Operator.AIRCOACH to routes)
    }

}
