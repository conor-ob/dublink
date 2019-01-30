package ie.dublinmapper.repository.swordsexpress

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.swordsexpress.SwordsExpressApi
import ie.dublinmapper.service.swordsexpress.SwordsExpressStopJson
import io.reactivex.Single

class SwordsExpressStopFetcher(
    private val api: SwordsExpressApi
) : Fetcher<List<SwordsExpressStopJson>, String> {

    override fun fetch(key: String): Single<List<SwordsExpressStopJson>> {
        return api.getAllStops()
    }

}
