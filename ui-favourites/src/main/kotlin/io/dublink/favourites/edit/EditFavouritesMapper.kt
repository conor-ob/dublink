package io.dublink.favourites.edit

import androidx.recyclerview.widget.ItemTouchHelper
import com.xwray.groupie.Section
import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.model.DividerItem

object EditFavouritesMapper {

    fun map(
        editing: List<DubLinkServiceLocation>,
        itemTouchHelper: ItemTouchHelper
    ) = Section(
        editing.map {
            mapServiceLocation(it, itemTouchHelper)
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
        serviceLocation: DubLinkServiceLocation,
        itemTouchHelper: ItemTouchHelper
    ) = Section(
        when (serviceLocation) {
            is DubLinkStopLocation -> listOf(
                EditableStopLocationItem(
                    serviceLocation = serviceLocation,
                    walkDistance = null,
                    itemTouchHelper = itemTouchHelper
                )
            )
            is DubLinkDockLocation -> listOf(
                EditableDockLocationItem(
                    serviceLocation = serviceLocation,
                    walkDistance = null,
                    itemTouchHelper = itemTouchHelper
                )
            )
            else -> emptyList()
        }
    )

    private fun mapDivider(items: Int, index: Int) = if (index < items - 1) DividerItem(
        index.toLong()
    ) else null
}
