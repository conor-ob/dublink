package ie.dublinmapper.repository.dublinbus

import ie.dublinmapper.domain.model.DublinBusStop
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.service.rtpi.RtpiBusStopOperatorInformationJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import java.util.*

object DublinBusStopMapper : Mapper<RtpiBusStopInformationJson, DublinBusStop> {

    override fun map(from: RtpiBusStopInformationJson): DublinBusStop {
        return DublinBusStop(
            from.stopId,
            from.fullName,
            Coordinate(from.latitude.toDouble(), from.longitude.toDouble()),
            mapOperators(from.operators),
            mapOperatorsToRoutes(from.operators)
        )
    }

//    private fun mapOperatorsToRoutes(operatorsByRoute: Map<String, Set<String>>): Map<Operator, Set<String>> {
//        val adaptedOperatorsByRoute = mutableMapOf<Operator, Set<String>>()
//        for (operator in operatorsByRoute) {
//            if (operator.key.equals(Operator.DUBLIN_BUS.shortName, ignoreCase = true)) {
//                adaptedOperatorsByRoute[Operator.DUBLIN_BUS] = operator.value
//            } else if (operator.key.equals(Operator.GO_AHEAD.shortName, ignoreCase = true)) {
//                adaptedOperatorsByRoute[Operator.GO_AHEAD] = operator.value
//            }
//        }
//        return adaptedOperatorsByRoute
//    }

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

//    private fun mapOperators(operatorsToRoute: Map<Operator, Set<String>>): EnumSet<Operator> {
//        val operators = EnumSet.noneOf(Operator::class.java)
//        operators.addAll(operatorsToRoute.keys)
//        return operators
//    }

    private fun mapOperators(operatorsJson: List<RtpiBusStopOperatorInformationJson>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        for (operatorJson in operatorsJson) {
            if (operatorJson.name.equals(Operator.DUBLIN_BUS.shortName, ignoreCase = true)) {
                operators.add(Operator.DUBLIN_BUS)
            } else if (operatorJson.name.equals(Operator.GO_AHEAD.shortName, ignoreCase = true)) {
                operators.add(Operator.GO_AHEAD)
            }
        }
        return operators
    }

    private fun mapOperatorsToRoutes(operatorsJson: List<RtpiBusStopOperatorInformationJson>): Map<Operator, Set<String>> {
        val operatorsByRoute = mutableMapOf<Operator, Set<String>>()
        for (operator in operatorsJson) {
            if (operator.name.equals(Operator.DUBLIN_BUS.shortName, ignoreCase = true)) {
                operatorsByRoute[Operator.DUBLIN_BUS] = operator.routes.toSet()
            } else if (operator.name.equals(Operator.GO_AHEAD.shortName, ignoreCase = true)) {
                operatorsByRoute[Operator.GO_AHEAD] = operator.routes.toSet()
            }
        }
        return operatorsByRoute
    }

}
