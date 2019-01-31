package ie.dublinmapper.repository.aircoach

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.aircoach.AircoachStopJson
import ie.dublinmapper.service.github.GithubApi
import io.reactivex.Single

class AircoachStopFetcher(
    private val api: GithubApi
) : Fetcher<List<AircoachStopJson>, String> {

    override fun fetch(key: String): Single<List<AircoachStopJson>> {
        return api.getAircoachStops()
    }

}
