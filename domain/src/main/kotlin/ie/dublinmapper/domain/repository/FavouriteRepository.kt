package ie.dublinmapper.domain.repository

import io.rtpi.api.Service

interface FavouriteRepository {

    fun saveFavourite(serviceLocationId: String, serviceLocationName: String, service: Service)

    fun removeFavourite(serviceLocationId: String, service: Service)
}
