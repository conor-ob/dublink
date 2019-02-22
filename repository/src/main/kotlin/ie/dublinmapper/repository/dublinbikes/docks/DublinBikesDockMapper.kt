package ie.dublinmapper.repository.dublinbikes.docks

import ie.dublinmapper.data.dublinbikes.DublinBikesDockEntity
import ie.dublinmapper.data.dublinbikes.DublinBikesDockLocationEntity
import ie.dublinmapper.data.dublinbikes.DublinBikesDockServiceEntity
import ie.dublinmapper.domain.model.DublinBikesDock
import ie.dublinmapper.service.jcdecaux.StationJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import java.util.*

object DublinBikesDockMapper {

    fun mapJsonToEntities(
        jsonArray: List<StationJson>
    ): Pair<List<DublinBikesDockLocationEntity>, List<DublinBikesDockServiceEntity>> {
        val locationEntities = mutableListOf<DublinBikesDockLocationEntity>()
        val serviceEntities = mutableListOf<DublinBikesDockServiceEntity>()
        for (json in jsonArray) {
            locationEntities.add(
                DublinBikesDockLocationEntity(
                    json.number.toString(),
                    json.address,
                    json.position.lat, json.position.lng
                )
            )
            serviceEntities.add(
                DublinBikesDockServiceEntity(
                    dockId = json.number.toString(),
                    operator = Operator.DUBLIN_BIKES.shortName,
                    docks = json.bikeStands,
                    availableBikes = json.availableBikes,
                    availableDocks = json.availableBikeStands
                )
            )
        }
        return Pair(locationEntities, serviceEntities)
    }

    fun mapEntitiesToStops(entities: List<DublinBikesDockEntity>): List<DublinBikesDock> {
        val docks = mutableListOf<DublinBikesDock>()
        for (entity in entities) {
            docks.add(
                DublinBikesDock(
                    id = entity.location.id,
                    name = entity.location.name,
                    coordinate = Coordinate(entity.location.latitude, entity.location.longitude),
                    operators = mapOperators(entity.services),
                    docks = entity.services[0].docks,
                    availableBikes = entity.services[0].availableBikes,
                    availableDocks = entity.services[0].availableDocks
                )
            )
        }
        return docks
    }

    private fun mapOperators(entities: List<DublinBikesDockServiceEntity>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        for (entity in entities) {
            operators.add(Operator.parse(entity.operator))
        }
        return operators
    }

}

//object DublinBikesDockMapper : Mapper<StationJson, DublinBikesDock> {
//
//    override fun map(from: StationJson): DublinBikesDock {
//        return DublinBikesDock(
//            from.number.toString(),
//            from.address,
//            Coordinate(from.position.lat, from.position.lng),
//            Operator.dublinBikes(),
//            from.availableBikes
//        )
//    }
//
//}
