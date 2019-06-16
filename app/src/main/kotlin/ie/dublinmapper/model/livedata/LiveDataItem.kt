package ie.dublinmapper.model.livedata

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.DueTime
import ie.dublinmapper.util.StringUtils
import kotlinx.android.synthetic.main.list_item_live_data.*

abstract class LiveDataItem(
    private val isEven: Boolean,
    private val isLast: Boolean
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        if (isLast) {
            viewHolder.rootView.background = viewHolder.itemView.context.getDrawable(R.drawable.shape_rounded_bottom_corners)
        } else {
            viewHolder.rootView.background = viewHolder.itemView.context.getDrawable(R.drawable.shape_rectangle)
        }
        if (isEven) {
            viewHolder.rootView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.white))
        } else {
            viewHolder.rootView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.grey_100))
        }
    }

    protected fun bindDueTimes(viewHolder: ViewHolder, dueTimes: List<DueTime>) {
        if (dueTimes.size == 1) {
            viewHolder.multipleDueTimesContainer.visibility = View.GONE
            viewHolder.singleDueTimeContainer.visibility = View.VISIBLE
            if (dueTimes[0].minutes == 0L) {
                viewHolder.dueTime.text = viewHolder.itemView.resources.getString(R.string.live_data_due)
            } else {
                viewHolder.dueTime.text = viewHolder.itemView.resources.getString(R.string.live_data_due_time, dueTimes[0].minutes)
            }
        } else {
            viewHolder.singleDueTimeContainer.visibility = View.GONE
            viewHolder.multipleDueTimesContainer.visibility = View.VISIBLE
            if (dueTimes[0].minutes == 0L) {
                viewHolder.firstDueTime.text = viewHolder.itemView.resources.getString(R.string.live_data_due)
            } else {
                viewHolder.firstDueTime.text = viewHolder.itemView.resources.getString(R.string.live_data_due_time, dueTimes[0].minutes)
            }
            viewHolder.laterDueTimes.text = viewHolder.itemView.resources.getString(R.string.live_data_due_times, StringUtils.join(
                dueTimes.subList(1, dueTimes.size).map { it.minutes.toString() }, ", "
            ))
        }
    }

}
