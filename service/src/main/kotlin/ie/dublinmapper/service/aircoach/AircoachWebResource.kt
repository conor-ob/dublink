package ie.dublinmapper.service.aircoach

import ie.dublinmapper.service.github.GithubApi
import io.reactivex.Single
import javax.inject.Inject

class AircoachWebResource @Inject constructor(
    private val scraper: AircoachScraper,
    private val api: AircoachApi,
    private val fallback: GithubApi
) : AircoachResource {

    override fun getStops(): Single<List<AircoachStopJson>> {
        try {
            return scraper.getStops()
        } catch (e: Exception) {

        }
        return fallback.getAircoachStops()
    }

    override fun getLiveData(id: String): Single<ServiceResponseJson> {
        return api.getLiveData(id)
    }

}
