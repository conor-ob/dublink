package ie.dublinmapper.model.livedata

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.util.StringUtils
import io.rtpi.api.LiveTime
import io.rtpi.api.TimedLiveData
import kotlinx.android.synthetic.main.list_item_live_data.*
import org.threeten.bp.Duration

abstract class LiveDataItem(
    private val isEven: Boolean,
    private val isLast: Boolean,
    private val isShowTime: Boolean = false //TODO
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

    protected fun bindDueTimes(viewHolder: ViewHolder, liveData: TimedLiveData) {
        if (liveData.liveTimes.size == 1) {
            viewHolder.multipleDueTimesContainer.visibility = View.GONE
            viewHolder.singleDueTimeContainer.visibility = View.VISIBLE
            bindSingleDueTime(viewHolder, liveData.liveTimes.first())
        } else {
            viewHolder.singleDueTimeContainer.visibility = View.GONE
            viewHolder.multipleDueTimesContainer.visibility = View.VISIBLE
            bindFirstDueTime(viewHolder, liveData.liveTimes.first())
            bindLaterDueTimes(viewHolder, liveData.liveTimes.subList(1, liveData.liveTimes.size))
        }
    }

    private fun bindSingleDueTime(viewHolder: ViewHolder, dueTime: LiveTime) {
        viewHolder.dueTime.text = getSingleDueTimeText(viewHolder, dueTime)
    }

    private fun bindFirstDueTime(viewHolder: ViewHolder, dueTime: LiveTime) {
        viewHolder.firstDueTime.text = getSingleDueTimeText(viewHolder, dueTime)
    }

    private fun bindLaterDueTimes(viewHolder: ViewHolder, dueTimes: List<LiveTime>) {
        viewHolder.laterDueTimes.text = getLaterDueTimesText(viewHolder, dueTimes)
    }

    private fun getSingleDueTimeText(viewHolder: ViewHolder, dueTime: LiveTime): String {
        return when {
            dueTime.waitTimeSeconds < Duration.ofMinutes(1).seconds -> viewHolder.itemView.resources.getString(R.string.live_data_due)
//            isShowTime -> dueTime.time.format(Formatter.hourMinute)
            else -> viewHolder.itemView.resources.getString(R.string.live_data_due_time,
                Duration.ofSeconds(dueTime.waitTimeSeconds.toLong()).toMinutes())
        }
    }

    private fun getLaterDueTimesText(viewHolder: ViewHolder, dueTimes: List<LiveTime>): String {
//        return if (isShowTime) {
//            viewHolder.itemView.resources.getString(
//                R.string.live_data_due_times_time,
//                StringUtils.join(dueTimes.map { it.time.format(Formatter.hourMinute) }, ", ")
//            )
//        } else {
//            viewHolder.itemView.resources.getString(
//                R.string.live_data_due_times,
//                StringUtils.join(dueTimes.map { it.minutes.toString() }, ", ")
//            )
//        }
        return viewHolder.itemView.resources.getString(
                R.string.live_data_due_times,
                StringUtils.join(dueTimes.map {
                    Duration.ofSeconds(it.waitTimeSeconds.toLong()).toMinutes().toString()
                }, ", ")
            )
    }

}
