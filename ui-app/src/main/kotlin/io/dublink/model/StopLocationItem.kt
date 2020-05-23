package io.dublink.model

import com.google.android.material.chip.ChipGroup
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.domain.model.DubLinkStopLocation
import io.dublink.domain.model.Filter
import io.dublink.ui.R
import io.dublink.util.ChipFactory

open class StopLocationItem(
    private val serviceLocation: DubLinkStopLocation,
    walkDistance: Double?
) : AbstractServiceLocationItem(serviceLocation, walkDistance) {

    override fun getLayout() = R.layout.list_item_stop_location

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)
        bindRoutes(viewHolder)
    }

    private fun bindRoutes(viewHolder: GroupieViewHolder) {
        viewHolder.itemView.findViewById<ChipGroup>(R.id.routes).apply {
            removeAllViewsInLayout()
            val activeFilters = if (serviceLocation.isFavourite) {
                serviceLocation.filters.filter { it.isActive }
            } else {
                serviceLocation.filters.filterIsInstance<Filter.RouteFilter>()
            }
            for (filter in activeFilters) {
                addView(ChipFactory.newChip(viewHolder.itemView.context, filter))
            }
        }
    }
}
