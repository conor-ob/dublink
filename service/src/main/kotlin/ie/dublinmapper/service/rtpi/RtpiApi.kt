package ie.dublinmapper.service.rtpi

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RtpiApi {

    @GET("busstopinformation")
    fun busStopInformation(@Query("operator") operator: String,
                 @Query("format") format: String) : Single<RtpiBusStopInformationResponseJson>

}
