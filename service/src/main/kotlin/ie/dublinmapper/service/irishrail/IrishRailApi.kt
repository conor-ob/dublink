package ie.dublinmapper.service.irishrail

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface IrishRailApi {

    @GET("getAllStationsXML_WithStationType")
    fun getAllStationsWithStationType(@Query("StationType") stationType: String) : Single<IrishRailStationResponseXml>

}
