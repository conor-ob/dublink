package ie.dublinmapper.favourites.edit

import com.xwray.groupie.Section
import ie.dublinmapper.favourites.R
import ie.dublinmapper.model.DividerItem
import ie.dublinmapper.model.ServiceLocationItem
import ie.dublinmapper.model.setSearchCandidate
import io.rtpi.api.DockLocation
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation
import io.rtpi.api.StopLocation

object EditFavouritesMapper {

    fun map(
        editing: List<ServiceLocation>
    ) = Section(
        editing.map {
            mapServiceLocation(it)
        }
    )

//    fun map(editing: List<ServiceLocation>): Section {
//        val items = editing.mapIndexedNotNull { index, serviceLocation ->
//            listOf(
//                mapServiceLocation(serviceLocation),
//                mapDivider(editing.size, index)
//            )
//        }
//        return Section(items.flatten())
//    }

    private fun mapServiceLocation(
        serviceLocation: ServiceLocation
    ) = Section(
        when (serviceLocation) {
            is StopLocation -> listOf(
                ServiceLocationItem(
                    serviceLocation = serviceLocation,
                    icon = mapIcon(serviceLocation.service),
                    routeGroups = serviceLocation.routeGroups,
                    walkDistance = null
                )
            )
            is DockLocation -> listOf(
                ServiceLocationItem(
                    serviceLocation = serviceLocation,
                    icon = mapIcon(serviceLocation.service),
                    routeGroups = emptyList(),
                    walkDistance = null
                ).apply {
                    setSearchCandidate()
                }
            )
            else -> emptyList()
        }
    )

    private fun mapDivider(items: Int, index: Int) = if (index < items - 1) DividerItem(index.toLong()) else null

    private fun mapIcon(service: Service): Int = when (service) {
        Service.AIRCOACH,
        Service.BUS_EIREANN,
        Service.DUBLIN_BUS -> R.drawable.ic_bus
        Service.DUBLIN_BIKES -> R.drawable.ic_bike
        Service.IRISH_RAIL -> R.drawable.ic_train
        Service.LUAS -> R.drawable.ic_tram
    }
}
