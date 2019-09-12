package ie.dublinmapper.repository.irishrail.stations

import ie.dublinmapper.datamodel.irishrail.IrishRailStationEntity
import ie.dublinmapper.domain.model.IrishRailStation
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object IrishRailStationEntityToDomainMapper : CustomConverter<IrishRailStationEntity, IrishRailStation>() {

    override fun convert(
        source: IrishRailStationEntity,
        destinationType: Type<out IrishRailStation>,
        mappingContext: MappingContext?
    ): IrishRailStation {
        return IrishRailStation(
            id = source.location.id,
            serviceLocationName = source.location.name,
            coordinate = Coordinate(source.location.latitude, source.location.longitude),
            operators = source.services.map { Operator.parse(it.operator) }.toSet(),
            service = Service.IRISH_RAIL
        )
    }

}
