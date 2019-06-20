package ie.dublinmapper.repository.luas.stops

import ie.dublinmapper.data.luas.LuasStopEntity
import ie.dublinmapper.data.luas.LuasStopServiceEntity
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Service
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type
import java.util.*

object LuasStopEntityToDomainMapper : CustomConverter<LuasStopEntity, LuasStop>() {

    override fun convert(
        source: LuasStopEntity,
        destinationType: Type<out LuasStop>,
        mappingContext: MappingContext
    ): LuasStop {
        return LuasStop(
            id = source.location.id,
            name = source.location.name,
            coordinate = Coordinate(source.location.latitude, source.location.longitude),
            operators = mapOperators(source.services),
            routes = mapOperatorsToRoutes(source.services),
            service = Service.LUAS
        )
    }

    private fun mapOperators(entities: List<LuasStopServiceEntity>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        for (entity in entities) {
            operators.add(Operator.parse(entity.operator))
        }
        return operators
    }

    private fun mapOperatorsToRoutes(entities: List<LuasStopServiceEntity>): Map<Operator, List<String>> {
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
