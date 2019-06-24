package ie.dublinmapper.repository.dart.stations

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.irishrail.IrishRailApi
import ie.dublinmapper.service.irishrail.IrishRailStationXml
import ie.dublinmapper.util.Service
import io.reactivex.Single

class DartStationFetcher(
    private val api: IrishRailApi,
    private val stationType: String
) : Fetcher<List<IrishRailStationXml>, Service> {

    override fun fetch(key: Service): Single<List<IrishRailStationXml>> {
        return api.getAllStationsXml().map { it.stations }
    }

}
