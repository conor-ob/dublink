package ie.dublinmapper.repository.luas.stops

import ie.dublinmapper.datamodel.luas.LuasStopEntity
import ie.dublinmapper.datamodel.luas.LuasStopServiceEntity
import ie.dublinmapper.domain.model.DetailedLuasStop
import io.rtpi.api.Coordinate
import io.rtpi.api.LuasStop
import io.rtpi.api.Operator
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type
import java.util.*

object LuasStopEntityToDomainMapper : CustomConverter<LuasStopEntity, DetailedLuasStop>() {

    override fun convert(
        source: LuasStopEntity,
        destinationType: Type<out DetailedLuasStop>,
        mappingContext: MappingContext
    ): DetailedLuasStop {
        return DetailedLuasStop(
            LuasStop(
                id = source.location.id,
                name = source.location.name,
                coordinate = Coordinate(source.location.latitude, source.location.longitude),
                operators = mapOperators(source.services),
                routes = mapOperatorsToRoutes(source.services)
            )
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
