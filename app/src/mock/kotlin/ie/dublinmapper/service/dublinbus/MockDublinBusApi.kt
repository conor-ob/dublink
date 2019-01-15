package ie.dublinmapper.service.dublinbus

import ie.dublinmapper.util.MockXmlUtils
import io.reactivex.Single

class MockDublinBusApi : DublinBusApi {

    override fun getAllDestinations(body: DublinBusDestinationRequestXml): Single<DublinBusDestinationResponseXml> {
        return singleResponse("dublin_bus_get_all_destinations.xml", DublinBusDestinationResponseXml::class.java)
    }

    override fun getRoutes(body: DublinBusRoutesRequestXml): Single<DublinBusRoutesResponseXml> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRealTimeStopData(body: DublinBusRealTimeStopDataRequestXml): Single<DublinBusRealTimeStopDataResponseXml> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getStopDataByRoute(body: DublinBusStopDataByRouteRequestXml): Single<DublinBusStopDataByRouteResponseXml> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun <T> singleResponse(fileName: String, type: Class<T>): Single<T> {
        return Single.just(MockXmlUtils.deserialize(fileName, type))
    }

}
