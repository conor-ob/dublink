package ie.dublinmapper.repository.dart.stations

import ie.dublinmapper.data.dart.DartStationEntity
import ie.dublinmapper.data.dart.DartStationLocationEntity
import ie.dublinmapper.data.dart.DartStationServiceEntity
import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.service.irishrail.IrishRailStationXml
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import java.util.*

object DartStationMapper {

    fun mapJsonToEntities(
        jsonArray: List<IrishRailStationXml>
    ): Pair<List<DartStationLocationEntity>, List<DartStationServiceEntity>> {
        val locationEntities = mutableListOf<DartStationLocationEntity>()
        val serviceEntities = mutableListOf<DartStationServiceEntity>()
        for (json in jsonArray) {
            locationEntities.add(
                DartStationLocationEntity(
                    id = json.code!!,
                    name = json.name!!,
                    latitude = json.latitude!!.toDouble(),
                    longitude = json.longitude!!.toDouble()
                )
            )
            when (json.code!!.toUpperCase()) {
                "BRAY",
                "CNLLY",
                "DLERY",
                "GSTNS",
                "MHIDE",
                "PERSE" -> {
                    serviceEntities.add(DartStationServiceEntity(stationId = json.code!!, operator = Operator.COMMUTER.shortName, route = Operator.COMMUTER.shortName))
                    serviceEntities.add(DartStationServiceEntity(stationId = json.code!!, operator = Operator.DART.shortName, route = Operator.DART.shortName))
                    serviceEntities.add(DartStationServiceEntity(stationId = json.code!!, operator = Operator.INTERCITY.shortName, route = Operator.INTERCITY.shortName))
                }
                "BROCK",
                "GCDK",
                "LDWNE" -> {
                    serviceEntities.add(DartStationServiceEntity(stationId = json.code!!, operator = Operator.COMMUTER.shortName, route = Operator.COMMUTER.shortName))
                    serviceEntities.add(DartStationServiceEntity(stationId = json.code!!, operator = Operator.DART.shortName, route = Operator.DART.shortName))
                }
                else -> {
                    serviceEntities.add(DartStationServiceEntity(stationId = json.code!!, operator = Operator.DART.shortName, route = Operator.DART.shortName))
                }
            }
        }
        return Pair(locationEntities, serviceEntities)
    }

    fun mapEntitiesToStations(entities: List<DartStationEntity>): List<DartStation> {
        val stops = mutableListOf<DartStation>()
        for (entity in entities) {
            stops.add(
                DartStation(
                    id = entity.location.id,
                    name = entity.location.name,
                    coordinate = Coordinate(entity.location.latitude, entity.location.longitude),
                    operators = mapOperators(entity.services)
                )
            )
        }
        return stops
    }

    private fun mapOperators(entities: List<DartStationServiceEntity>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        for (entity in entities) {
            operators.add(Operator.parse(entity.operator))
        }
        return operators
    }

}

//object DartStationMapper : Mapper<IrishRailStationXml, DartStation> {
//
//    override fun map(from: IrishRailStationXml): DartStation {
//        val operators = mapOperators(from.code!!)
//        return DartStation(
//            from.code!!,
//            from.name!!,
//            Coordinate(from.latitude!!.toDouble(), from.longitude!!.toDouble()),
//            operators
//        )
//    }
//
//    private fun mapOperators(stationCode: String): EnumSet<Operator> {
//        return when (stationCode.toUpperCase()) {
//            "BRAY",
//            "CNLLY",
//            "DLERY",
//            "GSTNS",
//            "MHIDE",
//            "PERSE" -> EnumSet.of(Operator.COMMUTER, Operator.DART, Operator.INTERCITY)
//            "BROCK",
//            "GCDK",
//            "LDWNE" -> EnumSet.of(Operator.COMMUTER, Operator.DART)
//            else -> Operator.dart()
//        }
//    }
//
//}
