package ie.dublinmapper.model.dart

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.model.DartLiveDataUi
import ie.dublinmapper.model.livedata.LiveDataItem
import ie.dublinmapper.util.Operator
import kotlinx.android.synthetic.main.list_item_live_data.*

class DartLiveDataItem(
    private val liveDataUi: DartLiveDataUi,
    isEven: Boolean,
    isLast: Boolean
) : LiveDataItem(isEven, isLast) {

    override fun getLayout() = R.layout.list_item_live_data

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        super.bindDueTimes(viewHolder, liveDataUi.liveData.dueTime)
        val liveData = liveDataUi.liveData
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
        if (other is DartLiveDataItem) {
            return liveDataUi.liveData.customHash == other.liveDataUi.liveData.customHash
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is DartLiveDataItem) {
            return liveDataUi.liveData == other.liveDataUi.liveData
        }
        return false
    }

    override fun hashCode(): Int {
        return liveDataUi.liveData.hashCode()
    }

}
