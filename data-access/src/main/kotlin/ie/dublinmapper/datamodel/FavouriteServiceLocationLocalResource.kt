package ie.dublinmapper.datamodel

import io.rtpi.api.Service

interface FavouriteServiceLocationLocalResource {

    fun insertFavourite(serviceLocationId: String, serviceLocationName: String, service: Service)

    fun deleteFavourite(serviceLocationId: String, service: Service)
}
