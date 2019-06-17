package ie.dublinmapper.repository.dart.stations

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.irishrail.IrishRailApi
import ie.dublinmapper.service.irishrail.IrishRailStationXml
import io.reactivex.Single

class DartStationFetcher(
    private val api: IrishRailApi,
    private val stationType: String
) : Fetcher<List<IrishRailStationXml>, String> {

    override fun fetch(key: String): Single<List<IrishRailStationXml>> {
        return api.getAllStationsXml().map { it.stations }
    }

}
