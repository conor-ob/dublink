package ie.dublinmapper.service.github

import ie.dublinmapper.service.aircoach.AircoachStopJson
import ie.dublinmapper.service.swordsexpress.SwordsExpressStopJson
import io.reactivex.Single
import retrofit2.http.GET

interface GithubApi {

    @GET("aircoach/stops.json")
    fun getAircoachStops(): Single<List<AircoachStopJson>>

    @GET("swords-express/stops.json")
    fun getSwordsExpressStops(): Single<List<SwordsExpressStopJson>>

}
