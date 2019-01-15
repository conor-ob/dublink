package ie.dublinmapper.service.rtpi

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RtpiApi {

    @GET("busstopinformation")
    fun busStopInformation(
        @Query("operator") operator: String,
        @Query("format") format: String
    ): Single<RtpiBusStopInformationResponseJson>

    @GET("routelistinformation/withvariants")
    fun routelistInformationWithVariants(
        @Query("format") format: String
    ): Single<RtpiRouteListInformationWithVariantsResponseJson>

    @GET("routeinformation")
    fun routeInformation(
        @Query("routeid") routeId: String,
        @Query("operator") operator: String,
        @Query("format") format: String
    ): Single<RtpiRouteInformationResponseJson>

    @GET("realtimebusinformation")
    fun realTimeBusInformation(
        @Query("stopid") stopId: String,
        @Query("operator") operator: String,
        @Query("format") format: String
    ): Single<RtpiRealTimeBusInformationResponseJson>

}
