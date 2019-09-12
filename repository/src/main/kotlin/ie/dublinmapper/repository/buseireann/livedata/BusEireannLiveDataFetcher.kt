package ie.dublinmapper.repository.buseireann.livedata

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import io.reactivex.Single

class BusEireannLiveDataFetcher(
    private val api: RtpiApi,
    private val operator: String,
    private val format: String
) : Fetcher<List<RtpiRealTimeBusInformationJson>, String> {

    override fun fetch(key: String): Single<List<RtpiRealTimeBusInformationJson>> {
        return api.realTimeBusInformation(key, operator, format).map { it.results }
    }

}
