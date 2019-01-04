package ie.dublinmapper.service.jcdecaux

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface JcdecauxApi {

    @GET("stations")
    fun getAllDocks(@Query("contract") cityName: String,
                    @Query("apiKey") apiKey: String): Observable<List<StationJson>>

}
