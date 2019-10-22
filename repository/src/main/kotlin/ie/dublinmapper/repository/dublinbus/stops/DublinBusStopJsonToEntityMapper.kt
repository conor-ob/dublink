package ie.dublinmapper.repository.dublinbus.stops

import ie.dublinmapper.datamodel.dublinbus.DublinBusStopEntity
import ie.dublinmapper.datamodel.dublinbus.DublinBusStopLocationEntity
import ie.dublinmapper.datamodel.dublinbus.DublinBusStopServiceEntity
import io.rtpi.api.DublinBusStop
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object DublinBusStopJsonToEntityMapper : CustomConverter<DublinBusStop, DublinBusStopEntity>() {

    override fun convert(
        source: DublinBusStop,
        destinationType: Type<out DublinBusStopEntity>,
        mappingContext: MappingContext
    ): DublinBusStopEntity {
        val locationEntity = DublinBusStopLocationEntity(
            id = source.id,
            name = source.name,
            latitude = source.coordinate.latitude,
            longitude = source.coordinate.longitude
        )
        val serviceEntities = mutableListOf<DublinBusStopServiceEntity>()
        for (entry in source.routes) {
            val operator = entry.key
            for (route in entry.value) {
                serviceEntities.add(
                    DublinBusStopServiceEntity(
                        stopId = source.id,
                        operator = operator.name,
                        route = route
                    )
                )
            }
        }
        val entity = DublinBusStopEntity(locationEntity)
        entity.services = serviceEntities
        return entity
    }

}
