package io.dublink.favourites.edit

import com.xwray.groupie.Section
import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.model.DividerItem
import io.dublink.model.DockLocationItem
import io.dublink.model.StopLocationItem

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
                StopLocationItem(
                    serviceLocation = serviceLocation,
                    walkDistance = null
                )
            )
            is DubLinkDockLocation -> listOf(
                DockLocationItem(
                    serviceLocation = serviceLocation,
                    walkDistance = null
                )
            )
            else -> emptyList()
        }
    )

    private fun mapDivider(items: Int, index: Int) = if (index < items - 1) DividerItem(
        index.toLong()
    ) else null
}
