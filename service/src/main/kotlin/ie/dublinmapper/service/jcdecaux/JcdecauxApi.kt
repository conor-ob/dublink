package ie.dublinmapper.service.jcdecaux

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface JcdecauxApi {

    @GET("stations")
    fun getAllDocks(@Query("contract") cityName: String,
                    @Query("apiKey") apiKey: String): Single<List<StationJson>>

}
