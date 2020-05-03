package ie.dublinmapper.domain.datamodel

import ie.dublinmapper.domain.model.DubLinkServiceLocation
import io.reactivex.Observable
import io.rtpi.api.Service

interface FavouriteServiceLocationLocalResource {

    fun getFavourites(): Observable<List<DubLinkServiceLocation>>

    fun insertFavourite(serviceLocation: DubLinkServiceLocation)

    fun deleteFavourite(serviceLocationId: String, service: Service)

    fun saveChanges(serviceLocations: List<DubLinkServiceLocation>)
}
