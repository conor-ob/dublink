package ie.dublinmapper.repository.luas.stops

import ie.dublinmapper.data.luas.LuasStopEntity
import ie.dublinmapper.data.luas.LuasStopLocationEntity
import ie.dublinmapper.data.luas.LuasStopServiceEntity
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service
import java.util.*

object LuasStopMapper {

    fun mapJsonToEntities(
        jsonArray: List<RtpiBusStopInformationJson>
    ): Pair<List<LuasStopLocationEntity>, List<LuasStopServiceEntity>> {
        val locationEntities = mutableListOf<LuasStopLocationEntity>()
        val serviceEntities = mutableListOf<LuasStopServiceEntity>()
        for (json in jsonArray) {
            locationEntities.add(
                LuasStopLocationEntity(
                    id = json.stopId,
                    name = json.fullName,
                    latitude = json.latitude.toDouble(),
                    longitude = json.longitude.toDouble()
                )
            )
            for (operator in json.operators) {
                for (route in operator.routes) {
                    serviceEntities.add(
                        LuasStopServiceEntity(
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

    fun mapEntitiesToStops(entities: List<LuasStopEntity>): List<LuasStop> {
        val stops = mutableListOf<LuasStop>()
        for (entity in entities) {
            stops.add(
                LuasStop(
                    id = entity.location.id,
                    name = entity.location.name,
                    coordinate = Coordinate(entity.location.latitude, entity.location.longitude),
                    operators = mapOperators(entity.services),
                    routes = mapOperatorsToRoutes(entity.services),
                    service = Service.LUAS
                )
            )
        }
        return stops
    }

    private fun mapOperators(entities: List<LuasStopServiceEntity>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        for (entity in entities) {
            operators.add(Operator.parse(entity.operator))
        }
        return operators
    }

    private fun mapOperatorsToRoutes(entities: List<LuasStopServiceEntity>): Map<Operator, List<String>> {
        val operatorsByRoute = mutableMapOf<Operator, List<String>>()
        for (entity in entities) {
            val operator = Operator.parse(entity.operator)
            val routes = operatorsByRoute[operator]
            if (routes == null) {
                operatorsByRoute[operator] = listOf(entity.route)
            } else {
                val newRoutes = routes.toMutableList()
                newRoutes.add(entity.route)
//                newRoutes.sortedWith(AlphanumComp)
                operatorsByRoute[operator] = newRoutes
            }
        }
        return operatorsByRoute
    }

}

//object LuasStopMapper : Mapper<RtpiBusStopInformationJson, LuasStop> {
//
//    override fun map(from: RtpiBusStopInformationJson): LuasStop {
//        return LuasStop(
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
