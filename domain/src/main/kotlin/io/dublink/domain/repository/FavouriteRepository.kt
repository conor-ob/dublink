package io.dublink.domain.repository

import io.dublink.domain.model.DubLinkServiceLocation
import io.reactivex.Observable
import io.rtpi.api.Service

interface FavouriteRepository {

    fun getFavourites(): Observable<List<DubLinkServiceLocation>>

    fun saveFavourites(serviceLocations: List<DubLinkServiceLocation>)

    fun removeFavourite(serviceLocationId: String, service: Service)
}
