package ie.dublinmapper.domain.datamodel

import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

interface ServiceLocationLocalResource {

    fun selectServiceLocations(service: Service): Observable<List<ServiceLocation>>

    fun insertServiceLocations(service: Service, serviceLocations: List<ServiceLocation>)
}
