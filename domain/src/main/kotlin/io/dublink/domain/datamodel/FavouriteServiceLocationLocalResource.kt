package io.dublink.domain.datamodel

import io.dublink.domain.model.DubLinkServiceLocation
import io.reactivex.Observable
import io.rtpi.api.Service

interface FavouriteServiceLocationLocalResource {

    fun getFavourites(): Observable<List<DubLinkServiceLocation>>

    fun saveFavourites(serviceLocations: List<DubLinkServiceLocation>)

    fun deleteFavourite(serviceLocationId: String, service: Service)
}
