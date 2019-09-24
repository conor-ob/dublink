package ie.dublinmapper.repository.luas.stops

import ie.dublinmapper.datamodel.luas.LuasStopEntity
import ie.dublinmapper.datamodel.luas.LuasStopLocationEntity
import ie.dublinmapper.datamodel.luas.LuasStopServiceEntity
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object LuasStopJsonToEntityMapper : CustomConverter<RtpiBusStopInformationJson, LuasStopEntity>() {

    override fun convert(
        source: RtpiBusStopInformationJson,
        destinationType: Type<out LuasStopEntity>,
        mappingContext: MappingContext
    ): LuasStopEntity {
        val locationEntity = LuasStopLocationEntity(
            id = source.stopId,
            name = source.fullName,
            latitude = source.latitude.toDouble(),
            longitude = source.longitude.toDouble()
        )
        val serviceEntities = mutableListOf<LuasStopServiceEntity>()
        for (operator in source.operators) {
            for (route in operator.routes) {
                serviceEntities.add(
                    LuasStopServiceEntity(
                        stopId = source.stopId,
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
