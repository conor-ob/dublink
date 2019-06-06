package ie.dublinmapper.repository.mapping

import ie.dublinmapper.data.dublinbus.DublinBusStopEntity
import ie.dublinmapper.data.dublinbus.DublinBusStopServiceEntity
import ie.dublinmapper.domain.model.DublinBusStop
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type
import java.util.*

object DublinBusStopEntityToDomainMapper : CustomConverter<DublinBusStopEntity, DublinBusStop>() {

    override fun convert(
        source: DublinBusStopEntity,
        destinationType: Type<out DublinBusStop>,
        mappingContext: MappingContext
    ): DublinBusStop {
        return DublinBusStop(
            id = source.location.id,
            name = source.location.name,
            coordinate = Coordinate(source.location.latitude, source.location.longitude),
            operators = convertOperators(source.services),
            routes = convertOperatorsToRoutes(source.services)
        )
    }

    private fun convertOperators(entities: List<DublinBusStopServiceEntity>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        for (entity in entities) {
            operators.add(Operator.parse(entity.operator))
        }
        return operators
    }

    private fun convertOperatorsToRoutes(entities: List<DublinBusStopServiceEntity>): Map<Operator, Set<String>> {
        val operatorsByRoute = mutableMapOf<Operator, Set<String>>()
        for (entity in entities) {
            val operator = Operator.parse(entity.operator)
            val routes = operatorsByRoute[operator]
            if (routes == null) {
                operatorsByRoute[operator] = setOf(entity.route)
            } else {
                val newRoutes = routes.toMutableSet()
                newRoutes.add(entity.route)
                operatorsByRoute[operator] = newRoutes
            }
        }
        return operatorsByRoute
    }

}
