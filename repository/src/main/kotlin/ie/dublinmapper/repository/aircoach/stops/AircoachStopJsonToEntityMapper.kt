package ie.dublinmapper.repository.aircoach.stops

import ie.dublinmapper.data.aircoach.AircoachStopEntity
import ie.dublinmapper.data.aircoach.AircoachStopLocationEntity
import ie.dublinmapper.data.aircoach.AircoachStopServiceEntity
import ie.dublinmapper.service.aircoach.AircoachStopJson
import ie.dublinmapper.util.Operator
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object AircoachStopJsonToEntityMapper : CustomConverter<AircoachStopJson, AircoachStopEntity>() {

    override fun convert(
        source: AircoachStopJson,
        destinationType: Type<out AircoachStopEntity>,
        mappingContext: MappingContext
    ): AircoachStopEntity {
        val locationEntity = AircoachStopLocationEntity(
            id = source.stopId,
            name = source.name,
            latitude = source.stopLatitude,
            longitude = source.stopLongitude
        )
        val serviceEntities = mutableListOf<AircoachStopServiceEntity>()
        for (service in source.services) {
            serviceEntities.add(
                AircoachStopServiceEntity(
                    stopId = source.stopId,
                    operator = Operator.AIRCOACH.shortName,
                    route = service.route
                )
            )
        }
        val entity = AircoachStopEntity(locationEntity)
        entity.services = serviceEntities
        return entity
    }

}
