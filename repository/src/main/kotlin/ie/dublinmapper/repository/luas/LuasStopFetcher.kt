package ie.dublinmapper.repository.luas

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import io.reactivex.Single

class LuasStopFetcher(
    private val api: RtpiApi,
    private val operator: String,
    private val format: String
) : Fetcher<List<RtpiBusStopInformationJson>, String> {

    override fun fetch(key: String): Single<List<RtpiBusStopInformationJson>> {
        return api.busStopInformation(operator, format).map { adapt(it.results) }
    }

    private fun adapt(stops: List<RtpiBusStopInformationJson>): List<RtpiBusStopInformationJson> {
        return stops.map { stop ->
            stop.copy(fullName = stop.fullName?.replace("LUAS", "")?.trim())
        }
    }

}
