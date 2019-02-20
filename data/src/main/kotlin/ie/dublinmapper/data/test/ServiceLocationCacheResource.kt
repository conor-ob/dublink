package ie.dublinmapper.data.test

import io.reactivex.Maybe

interface ServiceLocationCacheResource {

    fun selectServiceLocations(operator: String): Maybe<List<ServiceLocationEntity>>

    fun insertServiceLocations(serviceLocations: Pair<List<LocationEntity>, List<ServiceEntity>>)

}
