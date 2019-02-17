package ie.dublinmapper.repository.luas

import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.service.rtpi.RtpiBusStopOperatorInformationJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import java.util.EnumSet

object LuasStopMapper : Mapper<RtpiBusStopInformationJson, LuasStop> {

    override fun map(from: RtpiBusStopInformationJson): LuasStop {
        return LuasStop(
            from.stopId,
            from.fullName,
            Coordinate(from.latitude.toDouble(), from.longitude.toDouble()),
            mapOperators(from.operators),
            mapOperatorsToRoutes(from.operators)
        )
    }

    private fun mapOperators(operatorsJson: List<RtpiBusStopOperatorInformationJson>): EnumSet<Operator> {
        val operators = EnumSet.noneOf(Operator::class.java)
        for (operatorJson in operatorsJson) {
            operators.add(Operator.parse(operatorJson.name))
        }
        return operators
    }

    private fun mapOperatorsToRoutes(operatorsJson: List<RtpiBusStopOperatorInformationJson>): Map<Operator, Set<String>> {
        val operatorsByRoute = mutableMapOf<Operator, Set<String>>()
        for (operatorJson in operatorsJson) {
            val operator = Operator.parse(operatorJson.name)
            operatorsByRoute[operator] = operatorJson.routes.toSet()
        }
        return operatorsByRoute
    }

}
