package ie.dublinmapper.repository.dart.stations

import ie.dublinmapper.data.dart.DartStationEntity
import ie.dublinmapper.data.dart.DartStationLocationEntity
import ie.dublinmapper.data.dart.DartStationServiceEntity
import ie.dublinmapper.service.irishrail.IrishRailStationXml
import ie.dublinmapper.util.Operator
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object DartStationJsonToEntityMapper : CustomConverter<IrishRailStationXml, DartStationEntity>() {

    override fun convert(
        source: IrishRailStationXml,
        destinationType: Type<out DartStationEntity>,
        mappingContext: MappingContext
    ): DartStationEntity {
        val locationEntity = DartStationLocationEntity(
            id = source.code!!,
            name = source.name!!,
            latitude = source.latitude!!.toDouble(),
            longitude = source.longitude!!.toDouble()
        )
        val entity = DartStationEntity(locationEntity)
        entity.services = mapServices(source)
        return entity
    }

    //Hardcoded stuff... only takes into consideration stations on the DART line
    private fun mapServices(source: IrishRailStationXml): List<DartStationServiceEntity> {
        return when (source.code!!.toUpperCase()) {
            "BRAY",
            "CNLLY",
            "DLERY",
            "GSTNS",
            "MHIDE",
            "PERSE" -> {
                listOf(
                    DartStationServiceEntity(stationId = source.code!!, operator = Operator.COMMUTER.shortName),
                    DartStationServiceEntity(stationId = source.code!!, operator = Operator.DART.shortName),
                    DartStationServiceEntity(stationId = source.code!!, operator = Operator.INTERCITY.shortName)
                )
            }
            "BROCK",
            "GCDK",
            "LDWNE" -> {
                listOf(
                    DartStationServiceEntity(stationId = source.code!!, operator = Operator.COMMUTER.shortName),
                    DartStationServiceEntity(stationId = source.code!!, operator = Operator.DART.shortName)
                )
            }
            else -> {
                listOf(
                    DartStationServiceEntity(stationId = source.code!!, operator = Operator.DART.shortName)
                )
            }
        }
    }

}
