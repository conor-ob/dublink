package ie.dublinmapper.repository.swordsexpress.stops

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.github.GithubApi
import ie.dublinmapper.service.swordsexpress.SwordsExpressStopJson
import io.reactivex.Single

class SwordsExpressStopFetcher(
    private val api: GithubApi
) : Fetcher<List<SwordsExpressStopJson>, String> {

    override fun fetch(key: String): Single<List<SwordsExpressStopJson>> {
        return api.getSwordsExpressStops()
    }

}
