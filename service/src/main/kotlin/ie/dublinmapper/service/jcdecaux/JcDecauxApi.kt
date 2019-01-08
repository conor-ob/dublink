package ie.dublinmapper.service.jcdecaux

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JcDecauxApi {

    @GET("stations")
    fun stations(
        @Query("contract") cityName: String,
        @Query("apiKey") apiKey: String
    ): Single<List<StationJson>>

    @GET("stations/{station_number}")
    fun station(
        @Path("station_number") dockId: String,
        @Query("contract") cityName: String,
        @Query("apiKey") apiKey: String
    ): Single<StationJson>

}
