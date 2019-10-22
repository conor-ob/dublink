package ie.dublinmapper.repository.buseireann.stops

import ie.dublinmapper.datamodel.buseireann.BusEireannStopEntity
import ie.dublinmapper.datamodel.buseireann.BusEireannStopServiceEntity
import ie.dublinmapper.domain.model.DetailedBusEireannStop
import io.rtpi.api.BusEireannStop
import io.rtpi.api.Coordinate
import io.rtpi.api.Operator
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type
import java.util.*

object BusEireannStopEntityToDomainMapper : CustomConverter<BusEireannStopEntity, DetailedBusEireannStop>() {

    override fun convert(
        source: BusEireannStopEntity,
        destinationType: Type<out DetailedBusEireannStop>,
        mappingContext: MappingContext
    ): DetailedBusEireannStop {
        return DetailedBusEireannStop(
            BusEireannStop(
                id = source.location.id,
                name = source.location.name,
                coordinate = Coordinate(source.location.latitude, source.location.longitude),
                operators = mapOperators(source.services),
                routes = mapOperatorsToRoutes(source.services)
            )
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
