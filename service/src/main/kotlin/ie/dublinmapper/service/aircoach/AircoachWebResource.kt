package ie.dublinmapper.service.aircoach

import ie.dublinmapper.service.github.GithubApi
import io.reactivex.Single
import org.slf4j.LoggerFactory
import javax.inject.Inject

class AircoachWebResource @Inject constructor(
    private val scraper: AircoachScraper,
    private val api: AircoachApi,
    private val fallback: GithubApi
) : AircoachResource {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getStops(): Single<List<AircoachStopJson>> {
        try {
            return scraper.getStops()
        } catch (e: Exception) {
            log.error("Unable to scrape Aircoach stops", e)
        }
        return fallback.getAircoachStops()
    }

    private fun checkResponse(stops: List<AircoachStopJson>): Single<List<AircoachStopJson>> {
        if (stops.isEmpty()) {
            log.warn("Unable to scrape Aircoach stops")
            return fallback.getAircoachStops()
        }
        return Single.just(stops)
    }

    override fun getLiveData(id: String): Single<ServiceResponseJson> {
        return api.getLiveData(id)
    }

}
