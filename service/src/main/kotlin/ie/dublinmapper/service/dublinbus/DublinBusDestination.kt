package ie.dublinmapper.service.dublinbus

import org.simpleframework.xml.*

@Root(name = "soap12:Envelope")
@NamespaceList(
    Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance"),
    Namespace(prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema"),
    Namespace(prefix = "soap12", reference = "http://www.w3.org/2003/05/soap-envelope")
)
data class DublinBusDestinationRequestXml(
    @field:Element(name = "soap12:Body", required = false) val stopsRequestBodyXml: DublinBusDestinationRequestBodyXml
)

@Root(name = "soap12:Body", strict = false)
data class DublinBusDestinationRequestBodyXml(
    @field:Element(name = "GetAllDestinations", required = false) val stopsRequestRootXml: DublinBusDestinationRequestRootXml
)

@Root(name = "GetAllDestinations", strict = false)
@Namespace(reference = "http://dublinbus.ie/")
class DublinBusDestinationRequestRootXml

@Root(strict = false)
data class DublinBusDestinationResponseXml(
    @field:Path("soap:Body/GetAllDestinationsResponse/GetAllDestinationsResult/Destinations")
    @field:ElementList(inline = true) var stops: List<DublinBusDestinationXml> = mutableListOf()
)

@Root(name = "Destination", strict = false)
data class DublinBusDestinationXml(
    @field:Element(name = "StopNumber") var id: String? = null,
    @field:Element(name = "Description") var name: String? = null,
    @field:Element(name = "Latitude") var latitude: String? = null,
    @field:Element(name = "Longitude") var longitude: String? = null,
    @field:Element(name = "Routes", required = false) var routes: String? = null,
    @field:Element(name = "Type", required = false) var type: String? = null
)
