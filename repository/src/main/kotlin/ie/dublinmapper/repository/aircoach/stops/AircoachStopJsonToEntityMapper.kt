package ie.dublinmapper.repository.aircoach.stops

import ie.dublinmapper.datamodel.aircoach.AircoachStopEntity
import ie.dublinmapper.datamodel.aircoach.AircoachStopLocationEntity
import ie.dublinmapper.datamodel.aircoach.AircoachStopServiceEntity
import io.rtpi.api.AircoachStop
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object AircoachStopJsonToEntityMapper : CustomConverter<AircoachStop, AircoachStopEntity>() {

    override fun convert(
        source: AircoachStop,
        destinationType: Type<out AircoachStopEntity>,
        mappingContext: MappingContext
    ): AircoachStopEntity {
        val locationEntity = AircoachStopLocationEntity(
            id = source.id,
            name = source.name,
            latitude = source.coordinate.latitude,
            longitude = source.coordinate.longitude
        )
        val serviceEntities = mutableListOf<AircoachStopServiceEntity>()
        for (entry in source.routes) {
            val operator = entry.key
            for (route in entry.value) {
                serviceEntities.add(
                    AircoachStopServiceEntity(
                        stopId = source.id,
                        operator = operator.name,
                        route = route
                    )
                )
            }
        }
        val entity = AircoachStopEntity(locationEntity)
        entity.services = serviceEntities
        return entity
    }

}
