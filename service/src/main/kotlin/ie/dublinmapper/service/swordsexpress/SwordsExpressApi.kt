package ie.dublinmapper.service.swordsexpress

import io.reactivex.Single
import retrofit2.http.GET

interface SwordsExpressApi {

    @GET("stops.json")
    fun getAllStops(): Single<List<SwordsExpressStopJson>>

}
