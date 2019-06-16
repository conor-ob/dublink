package ie.dublinmapper.service.dublinbus

import org.simpleframework.xml.*

@Root(name = "soap12:Envelope")
@NamespaceList(
    Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance"),
    Namespace(prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema"),
    Namespace(prefix = "soap12", reference = "http://www.w3.org/2003/05/soap-envelope")
)
data class DublinBusRealTimeStopDataRequestXml(
    @field:Element(name = "soap12:Body", required = false) val dublinBusRealTimeStopDataRequestBodyXml: DublinBusRealTimeStopDataRequestBodyXml
)

@Root(name = "soap12:Body", strict = false)
data class DublinBusRealTimeStopDataRequestBodyXml(
    @field:Element(name = "GetRealTimeStopData", required = false) val dublinBusRealTimeStopDataRequestRootXml: DublinBusRealTimeStopDataRequestRootXml
)

@Root(name = "GetRealTimeStopData", strict = false)
@Namespace(reference = "http://dublinbus.ie/")
data class DublinBusRealTimeStopDataRequestRootXml(
    @field:Element(name = "stopId", required = false) val stopId: String,
    @field:Element(name = "forceRefresh", required = false) val forceRefresh: String
)

@Root(strict = false)
data class DublinBusRealTimeStopDataResponseXml(
    @field:Path("soap:Body/GetRealTimeStopDataResponse/GetRealTimeStopDataResult/diffgr:diffgram/DocumentElement")
    @field:ElementList(inline = true) var dublinBusRealTimeStopData: List<DublinBusRealTimeStopDataXml> = mutableListOf()
)

@Root(name = "StopData", strict = false)
data class DublinBusRealTimeStopDataXml(
    @field:Element(name = "MonitoredVehicleJourney_PublishedLineName") var routeId: String? = null,
    @field:Element(name = "MonitoredVehicleJourney_DestinationName") var destination: String? = null,
    @field:Element(name = "Timestamp") var timestamp: String? = null,
    @field:Element(name = "MonitoredCall_ExpectedArrivalTime") var expectedTimestamp: String? = null,
    @field:Element(name = "ServiceDelivery_ResponseTimestamp") var responseTimestamp: String? = null
)
