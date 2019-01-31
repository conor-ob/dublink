package ie.dublinmapper.service.aircoach

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface AircoachApi {

    @GET("api/eta/stops/{id}")
    fun getLiveData(
        @Path("id") id: String
    ): Single<ServiceResponseJson>

}
