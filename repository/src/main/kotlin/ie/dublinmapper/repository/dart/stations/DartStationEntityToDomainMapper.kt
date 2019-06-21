package ie.dublinmapper.repository.dart.stations

import ie.dublinmapper.datamodel.dart.DartStationEntity
import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object DartStationEntityToDomainMapper : CustomConverter<DartStationEntity, DartStation>() {

    override fun convert(
        source: DartStationEntity,
        destinationType: Type<out DartStation>,
        mappingContext: MappingContext?
    ): DartStation {
        return DartStation(
            id = source.location.id,
            name = source.location.name,
            coordinate = Coordinate(source.location.latitude, source.location.longitude),
            operators = source.services.map { Operator.parse(it.operator) }.toSet(),
            service = Service.IRISH_RAIL
        )
    }

}
