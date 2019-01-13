package ie.dublinmapper.repository.luas

import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.domain.repository.Mapper
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.Coordinate

object LuasStopMapper : Mapper<RtpiBusStopInformationJson, LuasStop> {

    override fun map(from: RtpiBusStopInformationJson): LuasStop {
        return LuasStop(
            from.stopId,
            from.fullName,
            Coordinate(from.latitude.toDouble(), from.longitude.toDouble())
        )
    }

}
