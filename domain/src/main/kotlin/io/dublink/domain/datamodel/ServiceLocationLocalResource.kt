package io.dublink.domain.datamodel

import io.dublink.domain.model.DubLinkServiceLocation
import io.reactivex.Observable
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

interface ServiceLocationLocalResource {

    fun selectServiceLocations(service: Service): Observable<List<DubLinkServiceLocation>>

    fun insertServiceLocations(service: Service, serviceLocations: List<ServiceLocation>)
}
