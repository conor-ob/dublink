package ie.dublinmapper.repository.buseireann

import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator

object BusEireannStopMapper : Mapper<RtpiBusStopInformationJson, BusEireannStop> {

    override fun map(from: RtpiBusStopInformationJson): BusEireannStop {
        return BusEireannStop(
            from.stopId,
            from.fullName,
            Coordinate(from.latitude.toDouble(), from.longitude.toDouble()),
            Operator.busEireann()
        )
    }

}
