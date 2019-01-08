package ie.dublinmapper.service.dublinbus

import org.simpleframework.xml.*

@Root(name = "soap12:Envelope")
@NamespaceList(
    Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance"),
    Namespace(prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema"),
    Namespace(prefix = "soap12", reference = "http://www.w3.org/2003/05/soap-envelope")
)
data class DublinBusStopDataByRouteRequestXml(
    @field:Element(name = "soap12:Body", required = false) val dublinBusStopDataByRouteRequestBodyXml: DublinBusStopDataByRouteRequestBodyXml
)

@Root(name = "soap12:Body", strict = false)
data class DublinBusStopDataByRouteRequestBodyXml(
    @field:Element(name = "GetStopDataByRoute", required = false) val dublinBusStopDataByRouteRequestRootXml: DublinBusStopDataByRouteRequestRootXml
)

@Root(name = "GetStopDataByRoute", strict = false)
@Namespace(reference = "http://dublinbus.ie/")
data class DublinBusStopDataByRouteRequestRootXml(
    @field:Element(name = "route", required = false) val routeId: String
)

@Root(strict = false)
data class DublinBusStopDataByRouteResponseXml(
    @field:Path("soap:Body/GetStopDataByRouteResponse/GetStopDataByRouteResult/diffgr:diffgram/StopDataByRoute")
    @field:Element(name = "Route") var dublinBusStopDataByRoute: DublinBusStopDataByRouteXml? = null,
    @field:Path("soap:Body/GetStopDataByRouteResponse/GetStopDataByRouteResult/diffgr:diffgram/StopDataByRoute")
    @field:ElementList(inline = true, required = false) var inboundStopXmls: List<DublinBusStopDataByRouteInboundStopXml> = mutableListOf(),
    @field:Path("soap:Body/GetStopDataByRouteResponse/GetStopDataByRouteResult/diffgr:diffgram/StopDataByRoute")
    @field:ElementList(inline = true, required = false) var outboundStopXmls: List<DublinBusStopDataByRouteOutboundStopXml> = mutableListOf()
)

@Root(name = "InboundStop", strict = false)
data class DublinBusStopDataByRouteInboundStopXml(
    @field:Element(name = "StopNumber", required = false) var id: String? = null
)

@Root(name = "OutboundStop", strict = false)
data class DublinBusStopDataByRouteOutboundStopXml(
    @field:Element(name = "StopNumber", required = false) var id: String? = null
)

@Root(name = "Route", strict = false)
data class DublinBusStopDataByRouteXml(
    @field:Element(name = "RouteNumber") var id: String? = null,
    @field:Element(name = "RouteDescription") var description: String? = null,
    @field:Element(name = "StartStageName") var origin: String? = null,
    @field:Element(name = "EndStageName") var destination: String? = null
)
