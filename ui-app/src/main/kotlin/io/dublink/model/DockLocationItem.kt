package io.dublink.model

import android.content.res.ColorStateList
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.chip.Chip
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.ui.R

class DockLocationItem(
    private val serviceLocation: DubLinkDockLocation,
    walkDistance: Double?
) : AbstractServiceLocationItem(serviceLocation, walkDistance) {

    override fun getLayout() = R.layout.list_item_dock_location

    override fun getDragDirs(): Int {
        return ItemTouchHelper.UP or ItemTouchHelper.DOWN
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)
        bindLiveData(viewHolder)
    }

    private fun bindLiveData(viewHolder: GroupieViewHolder) {
        viewHolder.itemView.findViewById<Chip>(R.id.bikesCount).apply {
            text = if (serviceLocation.availableBikes == 0) {
                " ${viewHolder.itemView.resources.getString(R.string.no)} "
            } else {
                " ${serviceLocation.availableBikes} "
            }
            setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(android.R.color.white, null)))
            setChipBackgroundColorResource(getBackgroundColour(serviceLocation.availableBikes))
        }
        viewHolder.itemView.findViewById<TextView>(R.id.bikes).apply {
            text = viewHolder.itemView.context.resources
                .getQuantityString(R.plurals.number_of_bikes, serviceLocation.availableBikes)
        }

        viewHolder.itemView.findViewById<Chip>(R.id.docksCount).apply {
            text = if (serviceLocation.availableDocks == 0) {
                " ${viewHolder.itemView.resources.getString(R.string.no)} "
            } else {
                " ${serviceLocation.availableDocks} "
            }
            setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(android.R.color.white, null)))
            setChipBackgroundColorResource(getBackgroundColour(serviceLocation.availableDocks))
        }
        viewHolder.itemView.findViewById<TextView>(R.id.docks).apply {
            text = viewHolder.itemView.context.resources
                .getQuantityString(R.plurals.number_of_docks, serviceLocation.availableDocks)
        }
    }

    private fun getBackgroundColour(amount: Int): Int {
        return when {
            amount < 2 -> R.color.luasRed
            amount < 5 -> R.color.aircoachOrange
            else -> R.color.dublinBikesTeal
        }
    }
}
