package ie.dublinmapper.model.dart

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.DartLiveData
import ie.dublinmapper.model.DartLiveDataUi
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.StringUtils
import kotlinx.android.synthetic.main.list_item_live_data_dart.direction_destination
import kotlinx.android.synthetic.main.list_item_live_data_dart.due
import kotlinx.android.synthetic.main.list_item_live_data_dart.train_type
import kotlinx.android.synthetic.main.list_item_live_data_dart.*

class DartLiveDataItem(
    private val liveDataUi: DartLiveDataUi,
    private val isEven: Boolean,
    private val isLast: Boolean
) : Item() {

    override fun getLayout() = R.layout.list_item_live_data_dart

    override fun bind(viewHolder: ViewHolder, position: Int) {
        if (isLast) {
            viewHolder.root.background = viewHolder.itemView.context.getDrawable(R.drawable.shape_rounded_bottom_corners)
        } else {
            viewHolder.root.background = viewHolder.itemView.context.getDrawable(R.drawable.shape_rectangle)
        }
        if (isEven) {
            viewHolder.root.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.white))
        } else {
            viewHolder.root.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.grey_200))
        }
        val liveData = liveDataUi.liveData
        viewHolder.train_type.text = liveData.operator.fullName
        when (liveData.operator) {
            Operator.DART -> {
                viewHolder.trainIconBorder.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.dartGreen))
            }
            Operator.COMMUTER -> {
                viewHolder.trainIconBorder.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.commuterBlue))
            }
            Operator.INTERCITY -> {
                viewHolder.trainIconBorder.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.intercityGrey))
            }
            else -> { }
        }
//        viewHolder.direction_destination.text = StringUtils.join(
//            Arrays.asList(
//                liveData.direction,
//                liveData.destination
//            ), " ${StringUtils.MIDDLE_DOT} ")
        viewHolder.direction_destination.text = liveData.destination
        if (liveData.dueTime[0].minutes == 0L) {
            viewHolder.due.text = viewHolder.itemView.resources.getString(R.string.live_data_due)
        } else {
            viewHolder.due.text = viewHolder.itemView.resources.getString(R.string.live_data_due_time, liveData.dueTime[0].minutes)
        }
        if (liveData.dueTime.size > 1) {
            viewHolder.due_later.text = viewHolder.itemView.resources.getString(R.string.live_data_due_times, StringUtils.join(
                liveData.dueTime.subList(1, liveData.dueTime.size).map { it.minutes.toString() }, ", "
            ))
        }
    }

}
