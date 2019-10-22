package ie.dublinmapper.repository.luas.stops

import ie.dublinmapper.datamodel.luas.LuasStopEntity
import ie.dublinmapper.datamodel.luas.LuasStopLocationEntity
import ie.dublinmapper.datamodel.luas.LuasStopServiceEntity
import io.rtpi.api.LuasStop
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object LuasStopJsonToEntityMapper : CustomConverter<LuasStop, LuasStopEntity>() {

    override fun convert(
        source: LuasStop,
        destinationType: Type<out LuasStopEntity>,
        mappingContext: MappingContext
    ): LuasStopEntity {
        val locationEntity = LuasStopLocationEntity(
            id = source.id,
            name = source.name,
            latitude = source.coordinate.latitude,
            longitude = source.coordinate.longitude
        )
        val serviceEntities = mutableListOf<LuasStopServiceEntity>()
        for (entry in source.routes) {
            val operator = entry.key
            for (route in entry.value) {
                serviceEntities.add(
                    LuasStopServiceEntity(
                        stopId = source.id,
                        operator = operator.name,
                        route = route
                    )
                )
            }
        }
        val entity = LuasStopEntity(locationEntity)
        entity.services = serviceEntities
        return entity
    }

}
