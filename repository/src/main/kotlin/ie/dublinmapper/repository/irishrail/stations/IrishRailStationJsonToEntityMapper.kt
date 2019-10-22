package ie.dublinmapper.repository.irishrail.stations

import ie.dublinmapper.datamodel.irishrail.IrishRailStationEntity
import ie.dublinmapper.datamodel.irishrail.IrishRailStationLocationEntity
import ie.dublinmapper.datamodel.irishrail.IrishRailStationServiceEntity
import io.rtpi.api.IrishRailStation
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object IrishRailStationJsonToEntityMapper : CustomConverter<IrishRailStation, IrishRailStationEntity>() {

    override fun convert(
        source: IrishRailStation,
        destinationType: Type<out IrishRailStationEntity>,
        mappingContext: MappingContext
    ): IrishRailStationEntity {
        val locationEntity = IrishRailStationLocationEntity(
            id = source.id,
            name = source.name,
            latitude = source.coordinate.latitude,
            longitude = source.coordinate.longitude
        )
        val serviceEntities = mutableListOf<IrishRailStationServiceEntity>()
        for (operator in source.operators) {
            serviceEntities.add(
                IrishRailStationServiceEntity(
                    stationId = source.id,
                    operator = operator.name
                )
            )
        }
        val entity = IrishRailStationEntity(locationEntity)
        entity.services = serviceEntities
        return entity
    }

}
