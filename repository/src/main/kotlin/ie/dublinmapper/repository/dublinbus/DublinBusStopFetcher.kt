package ie.dublinmapper.repository.dublinbus

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.dublinbus.DublinBusApi
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import io.reactivex.Single

class DublinBusStopFetcher(
    private val dublinBusApi: DublinBusApi,
    private val rtpiApi: RtpiApi,
    private val dublinBusOperator: String,
    private val goAheadOperator: String,
    private val format: String
) : Fetcher<List<RtpiBusStopInformationJson>, String> {

    override fun fetch(key: String): Single<List<RtpiBusStopInformationJson>> {
        return rtpiApi.busStopInformation(dublinBusOperator, format).map { it.results }
    }

}
