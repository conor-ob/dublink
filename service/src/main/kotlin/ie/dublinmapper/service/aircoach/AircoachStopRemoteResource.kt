package ie.dublinmapper.service.aircoach

import io.reactivex.Single

interface AircoachStopRemoteResource {

    fun getStops(): Single<List<AircoachStopJson>>

    fun getLiveData(id: String): Single<ServiceResponseJson>

}
