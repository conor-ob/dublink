package ie.dublinmapper.repository.dublinbikes.docks

import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockEntity
import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockLocationEntity
import ie.dublinmapper.datamodel.dublinbikes.DublinBikesDockServiceEntity
import io.rtpi.api.DublinBikesDock
import io.rtpi.api.Operator
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object DublinBikesDockJsonToEntityMapper : CustomConverter<DublinBikesDock, DublinBikesDockEntity>() {

    override fun convert(
        source: DublinBikesDock,
        destinationType: Type<out DublinBikesDockEntity>,
        mappingContext: MappingContext
    ): DublinBikesDockEntity {
        val locationEntity = DublinBikesDockLocationEntity(
            id = source.id,
            name = source.name,
            latitude = source.coordinate.latitude,
            longitude = source.coordinate.longitude
        )
        val serviceEntities = mutableListOf<DublinBikesDockServiceEntity>()
        serviceEntities.add(
            DublinBikesDockServiceEntity(
                dockId = source.id,
                operator = Operator.DUBLIN_BIKES.shortName,
                docks = source.docks,
                availableBikes = source.availableBikes,
                availableDocks = source.availableDocks
            )
        )
        val entity = DublinBikesDockEntity(locationEntity)
        entity.services = serviceEntities
        return entity
    }

}
