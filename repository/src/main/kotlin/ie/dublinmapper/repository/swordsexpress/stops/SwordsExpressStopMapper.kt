package ie.dublinmapper.repository.swordsexpress.stops

import ie.dublinmapper.datamodel.swordsexpress.SwordsExpressStopEntity
import ie.dublinmapper.datamodel.swordsexpress.SwordsExpressStopLocationEntity
import ie.dublinmapper.datamodel.swordsexpress.SwordsExpressStopServiceEntity
import ie.dublinmapper.domain.model.SwordsExpressStop
import ie.dublinmapper.service.swordsexpress.SwordsExpressStopJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service
import java.util.*

object SwordsExpressStopMapper {

    fun mapJsonToEntities(
        jsonArray: List<SwordsExpressStopJson>
    ): Pair<List<SwordsExpressStopLocationEntity>, List<SwordsExpressStopServiceEntity>> {
        val locationEntities = mutableListOf<SwordsExpressStopLocationEntity>()
        val serviceEntities = mutableListOf<SwordsExpressStopServiceEntity>()
        for (json in jsonArray) {
            locationEntities.add(
                SwordsExpressStopLocationEntity(
                    id = json.id,
                    name = json.name,
                    latitude = json.latitude,
                    longitude = json.longitude
                )
            )
            serviceEntities.add(
                SwordsExpressStopServiceEntity(
                    stopId = json.id,
                    operator = Operator.SWORDS_EXPRESS.shortName,
                    direction = json.direction
                )
            )
        }
        return Pair(locationEntities, serviceEntities)
    }

    fun mapEntitiesToStops(entities: List<SwordsExpressStopEntity>): List<SwordsExpressStop> {
        val stops = mutableListOf<SwordsExpressStop>()
        for (entity in entities) {
            stops.add(
                SwordsExpressStop(
                    id = entity.location.id,
                    name = entity.location.name,
                    coordinate = Coordinate(entity.location.latitude, entity.location.longitude),
                    operators = mapOperators(entity.services),
                    direction = entity.services[0].direction,
                    service = Service.SWORDS_EXPRESS
                )
            )
        }
        return stops
    }

    private fun mapOperators(entities: List<SwordsExpressStopServiceEntity>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        for (entity in entities) {
            operators.add(Operator.parse(entity.operator))
        }
        return operators
    }

}

//object SwordsExpressStopMapper : Mapper<SwordsExpressStopJson, SwordsExpressStop> {
//
//    override fun map(from: SwordsExpressStopJson): SwordsExpressStop {
//        return SwordsExpressStop(
//            id = from.id,
//            name = from.name,
//            coordinate = Coordinate(from.latitude, from.longitude),
//            direction = from.direction
//        )
//    }
//
//}
