package ie.dublinmapper.repository.irishrail.stations

import ie.dublinmapper.datamodel.irishrail.IrishRailStationEntity
import io.rtpi.api.Coordinate
import io.rtpi.api.IrishRailStation
import io.rtpi.api.Operator
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
            name = source.location.name,
            coordinate = Coordinate(source.location.latitude, source.location.longitude),
            operators = source.services.map { Operator.parse(it.operator) }.toSet()
        )
    }

}
