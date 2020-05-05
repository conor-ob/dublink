package io.dublink.favourites.edit

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import io.dublink.domain.model.DubLinkServiceLocation

class EditFavouritesAdapter<VH : GroupieViewHolder> : GroupAdapter<VH>() {

    private val serviceLocations = mutableListOf<DubLinkServiceLocation>()

    fun update(newServiceLocations: List<DubLinkServiceLocation>) {
        if (serviceLocations != newServiceLocations) {
            serviceLocations.clear()
            serviceLocations.addAll(newServiceLocations)
            update(listOf(EditFavouritesMapper.map(newServiceLocations)))
        }
    }

    fun getServiceLocations(): List<DubLinkServiceLocation> = serviceLocations

    fun getServiceLocation(position: Int): DubLinkServiceLocation = serviceLocations[position]
}
