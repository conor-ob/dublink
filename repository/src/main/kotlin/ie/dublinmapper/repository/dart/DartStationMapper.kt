package ie.dublinmapper.repository.dart

import ie.dublinmapper.domain.model.DartStation
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.irishrail.IrishRailStationXml
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import java.util.*

object DartStationMapper : Mapper<IrishRailStationXml, DartStation> {

    override fun map(from: IrishRailStationXml): DartStation {
        val operators = mapOperators(from.code!!)
        return DartStation(
            from.code!!,
            from.name!!,
            Coordinate(from.latitude!!.toDouble(), from.longitude!!.toDouble()),
            operators
        )
    }

    private fun mapOperators(stationCode: String): EnumSet<Operator> {
        return when (stationCode.toUpperCase()) {
            "BRAY",
            "CNLLY",
            "DLERY",
            "GSTNS",
            "MHIDE",
            "PERSE" -> EnumSet.of(Operator.COMMUTER, Operator.DART, Operator.INTERCITY)
            "BROCK",
            "GCDK",
            "LDWNE" -> EnumSet.of(Operator.COMMUTER, Operator.DART)
            else -> Operator.dart()
        }
    }

}
