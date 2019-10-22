package ie.dublinmapper.model.aircoach

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.model.livedata.LiveDataItem
import ie.dublinmapper.ui.R
import io.rtpi.api.AircoachLiveData
import kotlinx.android.synthetic.main.list_item_live_data.*
import org.threeten.bp.LocalTime

class AircoachLiveDataItem(
    private val liveData: AircoachLiveData,
    isEven: Boolean,
    isLast: Boolean
) : LiveDataItem(isEven, isLast) {

    override fun getLayout() = R.layout.list_item_live_data

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        super.bindDueTimes(viewHolder, liveData)
        val liveData = liveData
        viewHolder.title.text = liveData.route
        viewHolder.subtitle.text = liveData.destination
        viewHolder.operatorIconContainer.setImageResource(R.drawable.ic_bus)
        viewHolder.operatorIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.aircoachBlue))
    }

    override fun isSameAs(other: Item<*>?): Boolean {
        if (other is AircoachLiveDataItem) {
            return liveData.operator == other.liveData.operator &&
                    liveData.route == other.liveData.route &&
                    liveData.destination == other.liveData.destination
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is AircoachLiveDataItem) {
            return liveData == other.liveData
        }
        return false
    }

    override fun hashCode(): Int {
        return liveData.hashCode()
    }

}
