package ie.dublinmapper.repository.buseireann.stops

import ie.dublinmapper.data.buseireann.BusEireannStopEntity
import ie.dublinmapper.data.buseireann.BusEireannStopLocationEntity
import ie.dublinmapper.data.buseireann.BusEireannStopServiceEntity
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object BusEireannStopJsonToEntityMapper : CustomConverter<RtpiBusStopInformationJson, BusEireannStopEntity>() {

    override fun convert(
        source: RtpiBusStopInformationJson,
        destinationType: Type<out BusEireannStopEntity>,
        mappingContext: MappingContext
    ): BusEireannStopEntity {
        val locationEntity = BusEireannStopLocationEntity(
            id = source.stopId,
            name = source.fullName,
            latitude = source.latitude.toDouble(),
            longitude = source.longitude.toDouble()
        )
        val serviceEntities = mutableListOf<BusEireannStopServiceEntity>()
        for (operator in source.operators) {
            for (route in operator.routes) {
                serviceEntities.add(
                    BusEireannStopServiceEntity(
                        stopId = source.stopId,
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
