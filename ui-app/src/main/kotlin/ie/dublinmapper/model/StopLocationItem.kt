package ie.dublinmapper.model

import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.chip.ChipGroup
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.domain.model.DubLinkStopLocation
import ie.dublinmapper.domain.model.Filter
import ie.dublinmapper.ui.R
import ie.dublinmapper.util.ChipFactory

class StopLocationItem(
    private val serviceLocation: DubLinkStopLocation,
    walkDistance: Double?
) : AbstractServiceLocationItem(serviceLocation, walkDistance) {

    override fun getLayout() = R.layout.list_item_stop_location

    override fun getDragDirs(): Int {
        return ItemTouchHelper.UP or ItemTouchHelper.DOWN
    }

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
