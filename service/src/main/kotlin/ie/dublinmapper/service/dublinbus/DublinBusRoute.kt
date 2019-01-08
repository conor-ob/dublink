package ie.dublinmapper.service.dublinbus

import org.simpleframework.xml.*

@Root(name = "soap12:Envelope")
@NamespaceList(
    Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance"),
    Namespace(prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema"),
    Namespace(prefix = "soap12", reference = "http://www.w3.org/2003/05/soap-envelope")
)
data class DublinBusRoutesRequestXml(
    @field:Element(name = "soap12:Body", required = false) val dublinBusRoutesRequestBodyXml: DublinBusRoutesRequestBodyXml
)

@Root(name = "soap12:Body", strict = false)
data class DublinBusRoutesRequestBodyXml(
    @field:Element(name = "GetRoutes", required = false) val dublinBusRoutesRequestRootXml: DublinBusRoutesRequestRootXml
)

@Root(name = "GetRoutes", strict = false)
@Namespace(reference = "http://dublinbus.ie/")
data class DublinBusRoutesRequestRootXml(
    @field:Element(name = "filter", required = false) val filter: String
)

@Root(strict = false)
data class DublinBusRoutesResponseXml(
    @field:Path("soap:Body/GetRoutesResponse/GetRoutesResult/Routes")
    @field:ElementList(inline = true) var routes: List<DublinBusRouteXml> = mutableListOf()
)

@Root(name = "Route", strict = false)
data class DublinBusRouteXml(
    @field:Element(name = "Number") var id: String? = null,
    @field:Element(name = "From", required = false) var origin: String? = null,
    @field:Element(name = "Towards", required = false) var destination: String? = null
)
