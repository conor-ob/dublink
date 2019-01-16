package ie.dublinmapper.service.irishrail

import ie.dublinmapper.util.MockXmlUtils
import io.reactivex.Single

class MockIrishRailApi : IrishRailApi {

    override fun getAllStationsXmlWithStationType(stationType: String): Single<IrishRailStationResponseXml> {
        return singleResponse("irish_rail_get_all_stations_with_station_type.xml", IrishRailStationResponseXml::class.java)
    }

    override fun getStationDataByCodeXml(stationCode: String): Single<IrishRailStationDataResponseXml> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun <T> singleResponse(fileName: String, type: Class<T>): Single<T> {
        return Single.just(MockXmlUtils.deserialize(fileName, type))
    }

}
