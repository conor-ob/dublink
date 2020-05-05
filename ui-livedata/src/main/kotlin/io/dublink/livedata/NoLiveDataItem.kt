package io.dublink.livedata

import android.widget.TextView
import android.widget.updateText
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.model.AbstractCommonItem
import io.dublink.ui.R
import io.rtpi.api.Service

class NoLiveDataItem(
    private val service: Service?,
    id: Long
) : AbstractCommonItem(id) {

    override fun getLayout() = R.layout.list_item_no_live_data

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.no_live_data_message).apply {
            updateText(newText = mapMessage(service))
        }
    }

    private fun mapMessage(service: Service?): String {
        val mode = when (service) {
            Service.AIRCOACH,
            Service.BUS_EIREANN,
            Service.DUBLIN_BUS -> "buses"
            Service.IRISH_RAIL -> "trains"
            Service.LUAS -> "trams"
            else -> "arrivals"
        }
        return "No scheduled $mode"
    }
}
