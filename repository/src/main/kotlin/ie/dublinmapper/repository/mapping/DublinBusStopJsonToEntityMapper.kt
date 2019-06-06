package ie.dublinmapper.repository.mapping

import ie.dublinmapper.data.dublinbus.DublinBusStopEntity
import ie.dublinmapper.data.dublinbus.DublinBusStopLocationEntity
import ie.dublinmapper.data.dublinbus.DublinBusStopServiceEntity
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object DublinBusStopJsonToEntityMapper : CustomConverter<RtpiBusStopInformationJson, DublinBusStopEntity>() {

    override fun convert(
        source: RtpiBusStopInformationJson,
        destinationType: Type<out DublinBusStopEntity>,
        mappingContext: MappingContext
    ): DublinBusStopEntity {
        val locationEntity = DublinBusStopLocationEntity(
            id = source.stopId,
            name = source.fullName,
            latitude = source.latitude.toDouble(),
            longitude = source.longitude.toDouble()
        )
        val serviceEntities = mutableListOf<DublinBusStopServiceEntity>()
        for (sourceOperator in source.operators) {
            for (sourceRoute in sourceOperator.routes) {
                serviceEntities.add(
                    DublinBusStopServiceEntity(
                        stopId = source.stopId,
                        operator = sourceOperator.name,
                        route = sourceRoute
                    )
                )
            }
        }
        val entity = DublinBusStopEntity(locationEntity)
        entity.services = serviceEntities
        return entity
    }

}
