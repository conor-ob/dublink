package ie.dublinmapper.repository.buseireann.stops

import ie.dublinmapper.datamodel.buseireann.BusEireannStopEntity
import ie.dublinmapper.datamodel.buseireann.BusEireannStopServiceEntity
import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type
import java.util.*

object BusEireannStopEntityToDomainMapper : CustomConverter<BusEireannStopEntity, BusEireannStop>() {

    override fun convert(
        source: BusEireannStopEntity,
        destinationType: Type<out BusEireannStop>,
        mappingContext: MappingContext
    ): BusEireannStop {
        return BusEireannStop(
            id = source.location.id,
            serviceLocationName = source.location.name,
            coordinate = Coordinate(source.location.latitude, source.location.longitude),
            operators = mapOperators(source.services),
            service = Service.BUS_EIREANN,
            routes = mapOperatorsToRoutes(source.services)
        )
    }

    private fun mapOperators(entities: List<BusEireannStopServiceEntity>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        for (entity in entities) {
            operators.add(Operator.parse(entity.operator))
        }
        return operators
    }

    private fun mapOperatorsToRoutes(entities: List<BusEireannStopServiceEntity>): Map<Operator, List<String>> {
        val operatorsByRoute = mutableMapOf<Operator, List<String>>()
        for (entity in entities) {
            val operator = Operator.parse(entity.operator)
            val routes = operatorsByRoute[operator]
            if (routes == null) {
                operatorsByRoute[operator] = listOf(entity.route)
            } else {
                val newRoutes = routes.toMutableList()
                newRoutes.add(entity.route)
//                newRoutes.sortedWith(AlphanumComp)
                operatorsByRoute[operator] = newRoutes
            }
        }
        return operatorsByRoute
    }

}
