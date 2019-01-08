package ie.dublinmapper.repository.dart

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.irishrail.IrishRailApi
import ie.dublinmapper.service.irishrail.IrishRailStationDataXml
import io.reactivex.Single

class DartRealTimeDataFetcher(
    private val api: IrishRailApi
) : Fetcher<List<IrishRailStationDataXml>, String> {

    override fun fetch(key: String): Single<List<IrishRailStationDataXml>> {
        return api.getStationDataByCodeXml(key).map { it.stationData }
    }

}
