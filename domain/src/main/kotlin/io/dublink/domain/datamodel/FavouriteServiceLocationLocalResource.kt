package io.dublink.domain.datamodel

import io.dublink.domain.model.DubLinkServiceLocation
import io.reactivex.Observable
import io.rtpi.api.Service

interface FavouriteServiceLocationLocalResource {

    fun getFavourites(): Observable<List<DubLinkServiceLocation>>

    fun insertFavourite(serviceLocation: DubLinkServiceLocation)

    fun deleteFavourite(serviceLocationId: String, service: Service)

    fun saveChanges(serviceLocations: List<DubLinkServiceLocation>)
}