package ie.dublinmapper.repository.dublinbus.stops

import ie.dublinmapper.datamodel.dublinbus.DublinBusStopEntity
import ie.dublinmapper.datamodel.dublinbus.DublinBusStopServiceEntity
import ie.dublinmapper.domain.model.DetailedDublinBusStop
import io.rtpi.api.Coordinate
import io.rtpi.api.DublinBusStop
import io.rtpi.api.Operator
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type
import java.util.*

object DublinBusStopEntityToDomainMapper : CustomConverter<DublinBusStopEntity, DetailedDublinBusStop>() {

    override fun convert(
        source: DublinBusStopEntity,
        destinationType: Type<out DetailedDublinBusStop>,
        mappingContext: MappingContext
    ): DetailedDublinBusStop {
        return DetailedDublinBusStop(
            DublinBusStop(
                id = source.location.id,
                name = source.location.name,
                coordinate = Coordinate(source.location.latitude, source.location.longitude),
                operators = convertOperators(source.services),
                routes = convertOperatorsToRoutes(source.services)
            )
        )
    }

    private fun convertOperators(entities: List<DublinBusStopServiceEntity>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        for (entity in entities) {
            operators.add(Operator.parse(entity.operator))
        }
        return operators
    }

    private fun convertOperatorsToRoutes(entities: List<DublinBusStopServiceEntity>): Map<Operator, List<String>> {
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
