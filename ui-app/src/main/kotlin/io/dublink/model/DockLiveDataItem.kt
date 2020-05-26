package io.dublink.model

import android.content.res.ColorStateList
import android.widget.TextView
import com.google.android.material.chip.Chip
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.dublink.ui.R
import io.rtpi.api.DockLiveData

class DockLiveDataItem(
    private val liveData: DockLiveData
) : Item() {

    override fun getLayout() = R.layout.list_item_live_data_dublin_bikes

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        bindLiveData(viewHolder)
    }

    private fun bindLiveData(viewHolder: GroupieViewHolder) {
        viewHolder.itemView.findViewById<Chip>(R.id.bikesCount).apply {
            text = if (liveData.availableBikes == 0) {
                " ${viewHolder.itemView.resources.getString(R.string.no)} "
            } else {
                " ${liveData.availableBikes} "
            }
            val (bikesBackgroundColour, bikesTextColour) = mapColour(liveData.availableBikes)
            setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(bikesTextColour, null)))
            setChipBackgroundColorResource(bikesBackgroundColour)
        }
        viewHolder.itemView.findViewById<TextView>(R.id.bikes).apply {
            text = viewHolder.itemView.context.resources
                .getQuantityString(R.plurals.number_of_bikes, liveData.availableBikes)
        }

        viewHolder.itemView.findViewById<Chip>(R.id.docksCount).apply {
            text = if (liveData.availableDocks == 0) {
                " ${viewHolder.itemView.resources.getString(R.string.no)} "
            } else {
                " ${liveData.availableDocks} "
            }
            val (docksBackgroundColour, docksTextColour) = mapColour(liveData.availableDocks)
            setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(docksTextColour, null)))
            setChipBackgroundColorResource(docksBackgroundColour)
        }
        viewHolder.itemView.findViewById<TextView>(R.id.docks).apply {
            text = viewHolder.itemView.context.resources
                .getQuantityString(R.plurals.number_of_docks, liveData.availableDocks)
        }
    }

    private fun mapColour(amount: Int): Pair<Int, Int> =
        if (amount == 0) {
            R.color.error_red to R.color.dublin_bikes_brand_text
        } else {
            R.color.dublin_bikes_brand to R.color.dublin_bikes_brand_text
        }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        return equals(other)
    }

    override fun equals(other: Any?): Boolean {
        if (other is DockLiveDataItem) {
            return liveData == other.liveData
        }
        return false
    }

    override fun hashCode(): Int {
        return liveData.hashCode()
    }
}
