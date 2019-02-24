package ie.dublinmapper.repository.buseireann.stops

import ie.dublinmapper.data.buseireann.BusEireannStopEntity
import ie.dublinmapper.data.buseireann.BusEireannStopLocationEntity
import ie.dublinmapper.data.buseireann.BusEireannStopServiceEntity
import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import java.util.*

object BusEireannStopMapper {
    fun mapJsonToEntities(
        jsonArray: List<RtpiBusStopInformationJson>
    ): Pair<List<BusEireannStopLocationEntity>, List<BusEireannStopServiceEntity>> {
        val locationEntities = mutableListOf<BusEireannStopLocationEntity>()
        val serviceEntities = mutableListOf<BusEireannStopServiceEntity>()
        for (json in jsonArray) {
            locationEntities.add(
                BusEireannStopLocationEntity(
                    id = json.stopId,
                    name = json.fullName,
                    latitude = json.latitude.toDouble(),
                    longitude = json.longitude.toDouble()
                )
            )
            for (operator in json.operators) {
                for (route in operator.routes) {
                    serviceEntities.add(
                        BusEireannStopServiceEntity(
                            stopId = json.stopId,
                            operator = operator.name,
                            route = route
                        )
                    )
                }
            }
        }
        return Pair(locationEntities, serviceEntities)
    }

    fun mapEntitiesToStops(entities: List<BusEireannStopEntity>): List<BusEireannStop> {
        val stops = mutableListOf<BusEireannStop>()
        for (entity in entities) {
            stops.add(
                BusEireannStop(
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

    private fun mapOperators(entities: List<BusEireannStopServiceEntity>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        for (entity in entities) {
            operators.add(Operator.parse(entity.operator))
        }
        return operators
    }

    private fun mapOperatorsToRoutes(entities: List<BusEireannStopServiceEntity>): Map<Operator, Set<String>> {
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

//object BusEireannStopMapper : Mapper<RtpiBusStopInformationJson, BusEireannStop> {
//
//    override fun map(from: RtpiBusStopInformationJson): BusEireannStop {
//        return BusEireannStop(
//            from.stopId,
//            from.fullName,
//            Coordinate(from.latitude.toDouble(), from.longitude.toDouble()),
//            mapOperators(from.operators),
//            mapOperatorsToRoutes(from.operators)
//        )
//    }
//
//    private fun mapOperators(operatorsJson: List<RtpiBusStopOperatorInformationJson>): EnumSet<Operator> {
//        val operators = EnumSet.noneOf(Operator::class.java)
//        for (operatorJson in operatorsJson) {
//            operators.add(Operator.parse(operatorJson.name))
//        }
//        return operators
//    }
//
//    private fun mapOperatorsToRoutes(operatorsJson: List<RtpiBusStopOperatorInformationJson>): Map<Operator, Set<String>> {
//        val operatorsByRoute = mutableMapOf<Operator, Set<String>>()
//        for (operatorJson in operatorsJson) {
//            val operator = Operator.parse(operatorJson.name)
//            operatorsByRoute[operator] = operatorJson.routes.toSet()
//        }
//        return operatorsByRoute
//    }
//
//}
