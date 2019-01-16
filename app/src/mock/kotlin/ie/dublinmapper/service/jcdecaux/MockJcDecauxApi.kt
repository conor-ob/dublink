package ie.dublinmapper.service.jcdecaux

import ie.dublinmapper.util.MockJsonUtils
import io.reactivex.Single

class MockJcDecauxApi : JcDecauxApi {

    override fun stations(contract: String, apiKey: String): Single<List<StationJson>> {
//        return singleResponse("jc_decaux_stations.json", List<StationJson>::class.java)
        return Single.just(listOf(StationJson(1, "Blah", StationPositionJson(53.8, -6.2), 1, 1, 1)))
    }

    override fun station(stationNumber: String, contract: String, apiKey: String): Single<StationJson> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun <T> singleResponse(fileName: String, type: Class<T>): Single<T> {
        return Single.just(MockJsonUtils.deserialize(fileName, type))
    }

}
