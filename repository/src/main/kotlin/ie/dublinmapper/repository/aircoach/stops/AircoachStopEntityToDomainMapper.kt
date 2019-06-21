package ie.dublinmapper.repository.aircoach.stops

import ie.dublinmapper.datamodel.aircoach.AircoachStopEntity
import ie.dublinmapper.datamodel.aircoach.AircoachStopServiceEntity
import ie.dublinmapper.domain.model.AircoachStop
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type
import java.util.*

object AircoachStopEntityToDomainMapper : CustomConverter<AircoachStopEntity, AircoachStop>() {

    override fun convert(
        source: AircoachStopEntity,
        destinationType: Type<out AircoachStop>,
        mappingContext: MappingContext
    ): AircoachStop {
        return AircoachStop(
            id = source.location.id,
            name = source.location.name,
            coordinate = Coordinate(source.location.latitude, source.location.longitude),
            operators = mapOperators(source.services),
            service = Service.AIRCOACH,
            routes = mapOperatorsToRoutes(source.services)
        )
    }

    private fun mapOperators(entities: List<AircoachStopServiceEntity>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        for (entity in entities) {
            operators.add(Operator.parse(entity.operator))
        }
        return operators
    }

    private fun mapOperatorsToRoutes(entities: List<AircoachStopServiceEntity>): Map<Operator, List<String>> {
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
