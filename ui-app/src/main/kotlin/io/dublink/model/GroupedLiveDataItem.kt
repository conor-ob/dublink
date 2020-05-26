package io.dublink.model

import android.content.res.ColorStateList
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.dublink.ui.R
import io.dublink.util.ChipFactory
import io.rtpi.api.PredictionLiveData
import kotlinx.android.synthetic.main.list_item_live_data_grouped.*

class GroupedLiveDataItem(
    private val liveData: List<PredictionLiveData>
) : Item() {

    override fun getLayout() = R.layout.list_item_live_data_grouped

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        bindRoute(viewHolder)
        bindDestination(viewHolder)
        bindWaitTime(viewHolder)
    }

    private fun bindRoute(viewHolder: GroupieViewHolder) {
        viewHolder.groupedRoute.text = " ${liveData.first().routeInfo.route} "
        val (textColour, backgroundColour) = ChipFactory.mapColour(liveData.first().operator, liveData.first().routeInfo.route)
        viewHolder.groupedRoute.setTextColor(ColorStateList.valueOf(viewHolder.itemView.resources.getColor(textColour)))
        viewHolder.groupedRoute.setChipBackgroundColorResource(backgroundColour)
    }

    private fun bindDestination(viewHolder: GroupieViewHolder) {
//        when {
//            isStarting -> viewHolder.destination.text = liveData.destination
//            isTerminating -> viewHolder.destination.text = "from ${liveData.origin}"
//            else -> viewHolder.destination.text = liveData.destination
//        }
        viewHolder.groupedDestination.text = liveData.first().routeInfo.destination
    }

    private fun bindWaitTime(viewHolder: GroupieViewHolder) {
        viewHolder.groupedWaitTimeMinutes.text = if (liveData.size == 1) {
            val minutes = liveData.first().prediction.waitTime.toMinutes()
            if (minutes < 1L) {
                "Now"
            } else {
                "$minutes min"
            }
        } else {
            "${liveData.map {
                val minutes = it.prediction.waitTime.toMinutes()
                if (minutes < 1L) {
                    "Now"
                } else {
                    minutes
                }
            }.joinToString(", ")} min"
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other is GroupedLiveDataItem) {
            return liveData.first().operator == other.liveData.first().operator &&
                liveData.first().routeInfo.route == other.liveData.first().routeInfo.route &&
                liveData.first().routeInfo.destination == other.liveData.first().routeInfo.destination
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is GroupedLiveDataItem) {
            if (liveData.size == other.liveData.size) {
                for (index in liveData.indices) {
                    if (liveData[index].prediction.waitTime.toMinutes() != other.liveData[index].prediction.waitTime.toMinutes()) {
                        return false
                    }
                }
            }
            return true
        }
        return false
    }

    override fun hashCode(): Int {
        return liveData.hashCode()
    }
}
