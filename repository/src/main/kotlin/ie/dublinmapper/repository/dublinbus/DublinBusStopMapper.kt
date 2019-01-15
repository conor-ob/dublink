package ie.dublinmapper.repository.dublinbus

import ie.dublinmapper.domain.model.DublinBusStop
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.util.Operator
import java.util.*

object DublinBusStopMapper : Mapper<AggregatedStop, DublinBusStop> {

    override fun map(from: AggregatedStop): DublinBusStop {
        val operatorsToRoute = mapOperatorsToRoutes(from.operatorsByRoute)
        return DublinBusStop(
            from.id,
            from.name,
            from.coordinate,
            mapOperators(operatorsToRoute),
            operatorsToRoute
        )
    }

    private fun mapOperatorsToRoutes(operatorsByRoute: Map<String, Set<String>>): Map<Operator, Set<String>> {
        val adaptedOperatorsByRoute = mutableMapOf<Operator, Set<String>>()
        for (operator in operatorsByRoute) {
            if (operator.key.equals(Operator.DUBLIN_BUS.shortName, ignoreCase = true)) {
                adaptedOperatorsByRoute[Operator.DUBLIN_BUS] = operator.value
            } else if (operator.key.equals(Operator.GO_AHEAD.shortName, ignoreCase = true)) {
                adaptedOperatorsByRoute[Operator.GO_AHEAD] = operator.value
            }
        }
        return adaptedOperatorsByRoute
    }

//    private fun mapOperatorsToRoutes(operators: List<RtpiBusStopOperatorInformationJson>): Map<Operator, Set<String>> {
//        val operatorsToRoutes = mutableMapOf<Operator, Set<String>>()
//        for (operator in operators) {
//            if (operator.name.equals(Operator.DUBLIN_BUS.shortName, ignoreCase = true)) {
//                operatorsToRoutes[Operator.DUBLIN_BUS] = operator.routes.toSet()
//            } else if (operator.name.equals(Operator.GO_AHEAD.shortName, ignoreCase = true)) {
//                operatorsToRoutes[Operator.GO_AHEAD] = operator.routes.toSet()
//            }
//        }
//        return operatorsToRoutes
//    }

    private fun mapOperators(operatorsToRoute: Map<Operator, Set<String>>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        operators.addAll(operatorsToRoute.keys)
        return operators
    }

}
