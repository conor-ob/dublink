package ie.dublinmapper.service.irishrail

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "ArrayOfObjStation")
data class IrishRailStationResponseXml(
    @field:ElementList(name = "objStation", inline = true) var stations: List<IrishRailStationXml> = mutableListOf()
)

@Root(name = "objStation")
data class IrishRailStationXml(
    @field:Element(name = "StationDesc") var name: String? = null,
    @field:Element(name = "StationAlias", required = false) var alias: String? = null,
    @field:Element(name = "StationLatitude") var latitude: Double? = null,
    @field:Element(name = "StationLongitude") var longitude: Double? = null,
    @field:Element(name = "StationCode") var code: String? = null,
    @field:Element(name = "StationId") var id: String? = null
)
