package ie.dublinmapper.repository.swordsexpress.stops

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.github.GithubApi
import ie.dublinmapper.service.swordsexpress.SwordsExpressStopJson
import ie.dublinmapper.util.Service
import io.reactivex.Single

class SwordsExpressStopFetcher(
    private val api: GithubApi
) : Fetcher<List<SwordsExpressStopJson>, Service> {

    override fun fetch(key: Service): Single<List<SwordsExpressStopJson>> {
        return api.getSwordsExpressStops()
    }

}
