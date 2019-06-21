package ie.dublinmapper.repository.dublinbikes.docks

import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockEntity
import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockLocationEntity
import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockServiceEntity
import ie.dublinmapper.service.jcdecaux.StationJson
import ie.dublinmapper.util.Operator
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object DublinBikesDockJsonToEntityMapper : CustomConverter<StationJson, DublinBikesDockEntity>() {

    override fun convert(
        source: StationJson,
        destinationType: Type<out DublinBikesDockEntity>,
        mappingContext: MappingContext
    ): DublinBikesDockEntity {
        val locationEntity = DublinBikesDockLocationEntity(
            source.number.toString(),
            source.address,
            source.position.lat,
            source.position.lng
        )
        val serviceEntities = mutableListOf<DublinBikesDockServiceEntity>()
        serviceEntities.add(
            DublinBikesDockServiceEntity(
                dockId = source.number.toString(),
                operator = Operator.DUBLIN_BIKES.shortName,
                docks = source.bikeStands,
                availableBikes = source.availableBikes,
                availableDocks = source.availableBikeStands
            )
        )
        val entity = DublinBikesDockEntity(locationEntity)
        entity.services = serviceEntities
        return entity
    }

}
