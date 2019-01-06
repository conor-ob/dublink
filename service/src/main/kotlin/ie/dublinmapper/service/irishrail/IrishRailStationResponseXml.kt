package ie.dublinmapper.service.irishrail

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "ArrayOfObjStation")
data class IrishRailStationResponseXml(
    @field:ElementList(name = "objStation", inline = true) var stations: List<IrishRailStationXml>? = null
)
