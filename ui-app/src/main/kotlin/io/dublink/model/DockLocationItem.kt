package io.dublink.model

import android.content.res.ColorStateList
import android.widget.TextView
import com.google.android.material.chip.Chip
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.domain.model.DubLinkDockLocation
import io.dublink.ui.R

open class DockLocationItem(
    private val serviceLocation: DubLinkDockLocation,
    walkDistance: Double?
) : AbstractServiceLocationItem(serviceLocation, walkDistance) {

    override fun getLayout() = R.layout.list_item_dock_location

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
            val (bikesBackgroundColour, bikesTextColour) = mapColour(serviceLocation.availableBikes)
            setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(bikesTextColour, null)))
            setChipBackgroundColorResource(bikesBackgroundColour)
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
            val (docksBackgroundColour, docksTextColour) = mapColour(serviceLocation.availableDocks)
            setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(docksTextColour, null)))
            setChipBackgroundColorResource(docksBackgroundColour)
        }
        viewHolder.itemView.findViewById<TextView>(R.id.docks).apply {
            text = viewHolder.itemView.context.resources
                .getQuantityString(R.plurals.number_of_docks, serviceLocation.availableDocks)
        }
    }

    private fun mapColour(amount: Int): Pair<Int, Int> =
        if (amount == 0) {
            R.color.error_red to R.color.dublin_bikes_brand_text
        } else {
            R.color.dublin_bikes_brand to R.color.dublin_bikes_brand_text
        }
}
