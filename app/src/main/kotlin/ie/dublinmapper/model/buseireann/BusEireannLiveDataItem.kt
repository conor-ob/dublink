package ie.dublinmapper.model.buseireann

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.BusEireannLiveData
import ie.dublinmapper.model.livedata.LiveDataItem
import kotlinx.android.synthetic.main.list_item_live_data.*

class BusEireannLiveDataItem(
    private val liveData: BusEireannLiveData,
    isEven: Boolean,
    isLast: Boolean
) : LiveDataItem(isEven, isLast) {

    override fun getLayout() = R.layout.list_item_live_data

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        super.bindDueTimes(viewHolder, liveData.dueTime)
        val liveData = liveData
        viewHolder.title.text = liveData.route
        viewHolder.subtitle.text = liveData.destination
        viewHolder.operatorIconContainer.setImageResource(R.drawable.ic_bus)
        viewHolder.operatorIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.busEireannRed))
    }

    override fun isSameAs(other: Item<*>?): Boolean {
        if (other is BusEireannLiveDataItem) {
            return liveData.customHash == other.liveData.customHash
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is BusEireannLiveDataItem) {
            return liveData == other.liveData
        }
        return false
    }

    override fun hashCode(): Int {
        return liveData.hashCode()
    }

}
