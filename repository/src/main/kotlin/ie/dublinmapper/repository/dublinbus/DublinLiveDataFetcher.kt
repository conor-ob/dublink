package ie.dublinmapper.repository.dublinbus

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.dublinbus.DublinBusApi
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import io.reactivex.Single

class DublinLiveDataFetcher(
    private val dublinBusApi: DublinBusApi,
    private val rtpiApi: RtpiApi,
    private val dublinBusOperator: String,
    private val goAheadOperator: String,
    private val format: String
) : Fetcher<List<RtpiRealTimeBusInformationJson>, String> {

    override fun fetch(key: String): Single<List<RtpiRealTimeBusInformationJson>> {
        return rtpiApi.realTimeBusInformation(key, dublinBusOperator, format).map { it.results }
    }

}
