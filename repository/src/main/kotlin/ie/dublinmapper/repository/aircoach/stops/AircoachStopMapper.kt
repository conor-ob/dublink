package ie.dublinmapper.repository.aircoach.stops

import ie.dublinmapper.data.aircoach.AircoachStopEntity
import ie.dublinmapper.data.aircoach.AircoachStopLocationEntity
import ie.dublinmapper.data.aircoach.AircoachStopServiceEntity
import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.service.aircoach.AircoachStopJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import java.util.*

object AircoachStopMapper {

    fun mapJsonToEntities(
        jsonArray: List<AircoachStopJson>
    ): Pair<List<AircoachStopLocationEntity>, List<AircoachStopServiceEntity>> {
        val locationEntities = mutableListOf<AircoachStopLocationEntity>()
        val serviceEntities = mutableListOf<AircoachStopServiceEntity>()
        for (json in jsonArray) {
            locationEntities.add(
                AircoachStopLocationEntity(
                    id = json.stopId,
                    name = json.name,
                    latitude = json.stopLatitude,
                    longitude = json.stopLongitude
                )
            )
            for (service in json.services) {
                serviceEntities.add(
                    AircoachStopServiceEntity(
                        stopId = json.stopId,
                        operator = Operator.AIRCOACH.shortName,
                        route = service.route
                    )
                )
            }
        }
        return Pair(locationEntities, serviceEntities)
    }

    fun mapEntitiesToStops(entities: List<AircoachStopEntity>): List<AircoachStop> {
        val stops = mutableListOf<AircoachStop>()
        for (entity in entities) {
            stops.add(
                AircoachStop(
                    id = entity.location.id,
                    name = entity.location.name,
                    coordinate = Coordinate(entity.location.latitude, entity.location.longitude),
                    operators = mapOperators(entity.services),
                    routes = mapOperatorsToRoutes(entity.services)
                )
            )
        }
        return stops
    }

    private fun mapOperators(entities: List<AircoachStopServiceEntity>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        for (entity in entities) {
            operators.add(Operator.parse(entity.operator))
        }
        return operators
    }

    private fun mapOperatorsToRoutes(entities: List<AircoachStopServiceEntity>): Map<Operator, Set<String>> {
        val operatorsByRoute = mutableMapOf<Operator, Set<String>>()
        for (entity in entities) {
            val operator = Operator.parse(entity.operator)
            val routes = operatorsByRoute[operator]
            if (routes == null) {
                operatorsByRoute[operator] = setOf(entity.route)
            } else {
                val newRoutes = routes.toMutableSet()
                newRoutes.add(entity.route)
                operatorsByRoute[operator] = newRoutes
            }
        }
        return operatorsByRoute
    }

}

//object AircoachStopMapper : Mapper<AircoachStopJson, AircoachStop> {
//
//    override fun map(from: AircoachStopJson): AircoachStop {
//        return AircoachStop(
//            from.id,
//            from.name,
//            Coordinate(from.stopLatitude, from.stopLongitude),
//            Operator.aircoach(),
//            mapOperatorsToRoutes(from.services)
//        )
//    }
//
//    private fun mapOperatorsToRoutes(services: List<AircoachStopServiceJson>): Map<Operator, Set<String>> {
//        val routes = mutableSetOf<String>()
//        for (service in services) {
//            routes.add(service.route)
//        }
//        return mapOf(Operator.AIRCOACH to routes)
//    }
//
//}
