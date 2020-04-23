package ie.dublinmapper.favourites.edit

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import io.rtpi.api.ServiceLocation

class EditFavouritesAdapter<VH : GroupieViewHolder> : GroupAdapter<VH>() {

    private val serviceLocations = mutableListOf<ServiceLocation>()

    fun update(newServiceLocations: List<ServiceLocation>) {
        if (serviceLocations != newServiceLocations) {
            serviceLocations.clear()
            serviceLocations.addAll(newServiceLocations)
            update(listOf(EditFavouritesMapper.map(newServiceLocations)))
        }
    }

    fun getServiceLocations(): List<ServiceLocation> = serviceLocations

    fun getServiceLocation(position: Int): ServiceLocation = serviceLocations[position]
}
