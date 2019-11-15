package ie.dublinmapper.model.dart

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.LiveDataItem
import io.rtpi.api.IrishRailLiveData
import io.rtpi.api.Operator
import kotlinx.android.synthetic.main.list_item_live_data.*

open class IrishRailLiveDataItem(
    private val liveData: IrishRailLiveData,
    isEven: Boolean,
    isLast: Boolean
) : LiveDataItem(isEven, isLast) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bindDueTimes(viewHolder, liveData)
        val liveData = liveData
        viewHolder.title.text = liveData.destination
        viewHolder.subtitle.text = liveData.operator.fullName
        viewHolder.operatorIconContainer.setImageResource(R.drawable.ic_train)
        when (liveData.operator) {
            Operator.DART -> {
                viewHolder.operatorIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.dartGreen))
            }
            Operator.COMMUTER -> {
                viewHolder.operatorIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.commuterBlue))
            }
            Operator.INTERCITY -> {
                viewHolder.operatorIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.intercityYellow))
            }
            else -> { }
        }
    }

    override fun isSameAs(other: Item<*>?): Boolean {
        if (other is IrishRailLiveDataItem) {
            return liveData.operator == other.liveData.operator &&
                    liveData.route == other.liveData.route &&
                    liveData.destination == other.liveData.destination &&
                    liveData.direction == other.liveData.direction
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is IrishRailLiveDataItem) {
            return liveData == other.liveData
        }
        return false
    }

    override fun hashCode(): Int {
        return liveData.hashCode()
    }

}
