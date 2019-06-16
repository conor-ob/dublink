package ie.dublinmapper.model.dublinbus

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.model.DublinBusLiveDataUi
import ie.dublinmapper.model.livedata.LiveDataItem
import ie.dublinmapper.util.Operator
import kotlinx.android.synthetic.main.list_item_live_data.*

class DublinBusLiveDataItem(
    private val liveData: DublinBusLiveDataUi,
    isEven: Boolean,
    isLast: Boolean
) : LiveDataItem(isEven, isLast) {

    override fun getLayout() = R.layout.list_item_live_data

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        super.bindDueTimes(viewHolder, liveData.liveData.dueTime)
        val liveData = liveData.liveData
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

}
