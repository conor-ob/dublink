package ie.dublinmapper.favourites.edit

import com.xwray.groupie.Section
import ie.dublinmapper.domain.model.DubLinkDockLocation
import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.model.DubLinkStopLocation
import ie.dublinmapper.favourites.R
import ie.dublinmapper.model.DividerItem
import ie.dublinmapper.model.ServiceLocationItem
import ie.dublinmapper.model.setSearchCandidate
import io.rtpi.api.Service

object EditFavouritesMapper {

    fun map(
        editing: List<DubLinkServiceLocation>
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
        serviceLocation: DubLinkServiceLocation
    ) = Section(
        when (serviceLocation) {
            is DubLinkStopLocation -> listOf(
                ServiceLocationItem(
                    serviceLocation = serviceLocation,
                    icon = mapIcon(serviceLocation.service),
                    walkDistance = null
                )
            )
            is DubLinkDockLocation -> listOf(
                ServiceLocationItem(
                    serviceLocation = serviceLocation,
                    icon = mapIcon(serviceLocation.service),
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
