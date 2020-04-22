package ie.dublinmapper.domain.repository

import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

interface FavouriteRepository {

    fun saveFavourite(serviceLocationId: String, serviceLocationName: String, service: Service)

    fun removeFavourite(serviceLocationId: String, service: Service)

    fun nameToBeDetermined(serviceLocation: ServiceLocation)
}
