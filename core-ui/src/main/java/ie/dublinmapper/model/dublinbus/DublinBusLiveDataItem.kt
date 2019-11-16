package ie.dublinmapper.model.dublinbus

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.LiveDataItem
import io.rtpi.api.DublinBusLiveData
import io.rtpi.api.Operator
import kotlinx.android.synthetic.main.list_item_live_data.*

class DublinBusLiveDataItem(
    private val liveData: DublinBusLiveData,
    isEven: Boolean,
    isLast: Boolean
) : LiveDataItem(isEven, isLast) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bindDueTimes(viewHolder, liveData)
        val liveData = liveData
        viewHolder.title.text = liveData.route
        viewHolder.subtitle.text = liveData.destination
        viewHolder.operatorIconContainer.setImageResource(R.drawable.ic_bus)
        when (liveData.operator) {
            Operator.DUBLIN_BUS -> {
                viewHolder.operatorIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.dublinBusYellow))
            }
            Operator.GO_AHEAD -> {
                viewHolder.operatorIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.goAheadBlue))
            }
            else -> { }
        }
    }

    override fun isSameAs(other: Item<*>?): Boolean {
        if (other is DublinBusLiveDataItem) {
            return liveData.operator == other.liveData.operator &&
                    liveData.route == other.liveData.route &&
                    liveData.destination == other.liveData.destination
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is DublinBusLiveDataItem) {
            return liveData == other.liveData
        }
        return false
    }

    override fun hashCode(): Int {
        return liveData.hashCode()
    }

}
