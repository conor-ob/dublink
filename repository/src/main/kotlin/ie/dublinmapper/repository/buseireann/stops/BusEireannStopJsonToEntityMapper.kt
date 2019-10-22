package ie.dublinmapper.repository.buseireann.stops

import ie.dublinmapper.datamodel.buseireann.BusEireannStopEntity
import ie.dublinmapper.datamodel.buseireann.BusEireannStopLocationEntity
import ie.dublinmapper.datamodel.buseireann.BusEireannStopServiceEntity
import io.rtpi.api.BusEireannStop
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object BusEireannStopJsonToEntityMapper : CustomConverter<BusEireannStop, BusEireannStopEntity>() {

    override fun convert(
        source: BusEireannStop,
        destinationType: Type<out BusEireannStopEntity>,
        mappingContext: MappingContext
    ): BusEireannStopEntity {
        val locationEntity = BusEireannStopLocationEntity(
            id = source.id,
            name = source.name,
            latitude = source.coordinate.latitude,
            longitude = source.coordinate.longitude
        )
        val serviceEntities = mutableListOf<BusEireannStopServiceEntity>()
        for (entry in source.routes) {
            val operator = entry.key
            for (route in entry.value) {
                serviceEntities.add(
                    BusEireannStopServiceEntity(
                        stopId = source.id,
                        operator = operator.name,
                        route = route
                    )
                )
            }
        }
        val entity = BusEireannStopEntity(locationEntity)
        entity.services = serviceEntities
        return entity
    }

}
