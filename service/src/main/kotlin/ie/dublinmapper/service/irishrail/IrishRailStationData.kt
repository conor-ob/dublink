package ie.dublinmapper.service.irishrail

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "ArrayOfObjStationData")
data class IrishRailStationDataResponseXml(
    @field:ElementList(name = "objStationData", inline = true, required = false) var stationData: List<IrishRailStationDataXml> = mutableListOf()
)

@Root(name = "objStationData")
data class IrishRailStationDataXml(
    @field:Element(name = "Servertime", required = false) var serverTime: String? = null,
    @field:Element(name = "Traincode", required = false) var trainCode: String? = null,
    @field:Element(name = "Stationfullname", required = false) var stationName: String? = null,
    @field:Element(name = "Stationcode", required = false) var stationCode: String? = null,
    @field:Element(name = "Querytime", required = false) var queryTime: String? = null,
    @field:Element(name = "Traindate", required = false) var trainDate: String? = null,
    @field:Element(name = "Origin", required = false) var origin: String? = null,
    @field:Element(name = "Destination") var destination: String? = null,
    @field:Element(name = "Origintime", required = false) var originTime: String? = null,
    @field:Element(name = "Destinationtime", required = false) var destinationTime: String? = null,
    @field:Element(name = "Status", required = false) var status: String? = null,
    @field:Element(name = "Lastlocation", required = false) var lastLocation: String? = null,
    @field:Element(name = "Duein") var dueIn: String? = null,
    @field:Element(name = "Late", required = false) var late: String? = null,
    @field:Element(name = "Exparrival", required = false) var expectedArrivalTime: String? = null,
    @field:Element(name = "Expdepart", required = false) var expectedDepartureTime: String? = null,
    @field:Element(name = "Scharrival", required = false) var scheduledArrivalTime: String? = null,
    @field:Element(name = "Schdepart", required = false) var scheduledDepartureTime: String? = null,
    @field:Element(name = "Direction") var direction: String? = null,
    @field:Element(name = "Traintype") var trainType: String? = null,
    @field:Element(name = "Locationtype", required = false) var locationType: String? = null
)
