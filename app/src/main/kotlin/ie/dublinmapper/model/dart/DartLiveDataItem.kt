package ie.dublinmapper.model.dart

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.model.DartLiveDataUi
import ie.dublinmapper.model.livedata.LiveDataItem
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.StringUtils
import kotlinx.android.synthetic.main.list_item_live_data.*

class DartLiveDataItem(
    private val liveDataUi: DartLiveDataUi,
    isEven: Boolean,
    isLast: Boolean
) : LiveDataItem(isEven, isLast) {

    override fun getLayout() = R.layout.list_item_live_data

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        val liveData = liveDataUi.liveData
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
                viewHolder.operatorIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.intercityGrey))
            }
            else -> { }
        }
        viewHolder.title.text = liveData.destination

        if (liveData.dueTime.size == 1) {
            viewHolder.multipleDueTimesContainer.visibility = View.GONE
            viewHolder.singleDueTimeContainer.visibility = View.VISIBLE
            if (liveData.dueTime[0].minutes == 0L) {
                viewHolder.dueTime.text = viewHolder.itemView.resources.getString(R.string.live_data_due)
            } else {
                viewHolder.dueTime.text = viewHolder.itemView.resources.getString(R.string.live_data_due_time, liveData.dueTime[0].minutes)
            }
        } else {
            viewHolder.singleDueTimeContainer.visibility = View.GONE
            viewHolder.multipleDueTimesContainer.visibility = View.VISIBLE
            if (liveData.dueTime[0].minutes == 0L) {
                viewHolder.firstDueTime.text = viewHolder.itemView.resources.getString(R.string.live_data_due)
            } else {
                viewHolder.firstDueTime.text = viewHolder.itemView.resources.getString(R.string.live_data_due_time, liveData.dueTime[0].minutes)
            }
            viewHolder.laterDueTimes.text = viewHolder.itemView.resources.getString(R.string.live_data_due_times, StringUtils.join(
                liveData.dueTime.subList(1, liveData.dueTime.size).map { it.minutes.toString() }, ", "
            ))
        }
    }

}
