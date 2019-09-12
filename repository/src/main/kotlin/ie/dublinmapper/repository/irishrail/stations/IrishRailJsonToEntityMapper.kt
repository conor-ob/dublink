package ie.dublinmapper.repository.irishrail.stations

import ie.dublinmapper.datamodel.irishrail.IrishRailStationEntity
import ie.dublinmapper.datamodel.irishrail.IrishRailStationLocationEntity
import ie.dublinmapper.datamodel.irishrail.IrishRailStationServiceEntity
import ie.dublinmapper.service.irishrail.IrishRailStationXml
import ie.dublinmapper.util.Operator
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object IrishRailJsonToEntityMapper : CustomConverter<IrishRailStationXml, IrishRailStationEntity>() {

    override fun convert(
        source: IrishRailStationXml,
        destinationType: Type<out IrishRailStationEntity>,
        mappingContext: MappingContext
    ): IrishRailStationEntity {
        val locationEntity = IrishRailStationLocationEntity(
            id = source.code!!,
            name = source.name!!,
            latitude = source.latitude!!.toDouble(),
            longitude = source.longitude!!.toDouble()
        )
        val entity = IrishRailStationEntity(locationEntity)
        entity.services = mapServices(source)
        return entity
    }

    //Hardcoded stuff... only takes into consideration stations on the DART line
    private fun mapServices(source: IrishRailStationXml): List<IrishRailStationServiceEntity> {
        return when (source.code!!.toUpperCase()) {
            "BRAY",
            "CNLLY",
            "DLERY",
            "GSTNS",
            "MHIDE",
            "PERSE" -> {
                listOf(
                    IrishRailStationServiceEntity(stationId = source.code!!, operator = Operator.COMMUTER.shortName),
                    IrishRailStationServiceEntity(stationId = source.code!!, operator = Operator.DART.shortName),
                    IrishRailStationServiceEntity(stationId = source.code!!, operator = Operator.INTERCITY.shortName)
                )
            }
            "BROCK",
            "GCDK",
            "LDWNE" -> {
                listOf(
                    IrishRailStationServiceEntity(stationId = source.code!!, operator = Operator.COMMUTER.shortName),
                    IrishRailStationServiceEntity(stationId = source.code!!, operator = Operator.DART.shortName)
                )
            }
            else -> {
                listOf(
                    IrishRailStationServiceEntity(stationId = source.code!!, operator = Operator.DART.shortName)
                )
            }
        }
    }

}
