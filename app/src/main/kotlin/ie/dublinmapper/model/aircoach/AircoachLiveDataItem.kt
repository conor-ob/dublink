package ie.dublinmapper.model.aircoach

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.model.AircoachLiveDataUi
import ie.dublinmapper.model.livedata.LiveDataItem
import kotlinx.android.synthetic.main.list_item_live_data.*

class AircoachLiveDataItem(
    private val liveData: AircoachLiveDataUi,
    isEven: Boolean,
    isLast: Boolean
) : LiveDataItem(isEven, isLast) {

    override fun getLayout() = R.layout.list_item_live_data

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        super.bindDueTimes(viewHolder, liveData.liveData.dueTime)
        val liveData = liveData.liveData
        viewHolder.title.text = liveData.route
        viewHolder.subtitle.text = liveData.operator.fullName
        viewHolder.operatorIconContainer.setImageResource(R.drawable.ic_bus)
        viewHolder.operatorIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.aircoachBlue))
    }

}
