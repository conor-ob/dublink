package ie.dublinmapper.model.dart

import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.DartLiveData
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.StringUtils
import kotlinx.android.synthetic.main.list_item_live_data_dart.*
import java.util.*

class DartLiveDataItem(
    private val liveData: DartLiveData
) : Item() {

    override fun getLayout() = R.layout.list_item_live_data_dart

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.train_type.text = liveData.operator.fullName
        when (liveData.operator) {
            Operator.DART -> viewHolder.train_type.setTextColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.dartGreen))
            Operator.COMMUTER -> viewHolder.train_type.setTextColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.commuterBlue))
            Operator.INTERCITY -> viewHolder.train_type.setTextColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.intercityGrey))
            else -> { }
        }
        viewHolder.direction_destination.text = StringUtils.join(
            Arrays.asList(
                liveData.direction,
                liveData.destination
            ), " ${StringUtils.MIDDLE_DOT} ")
        if (liveData.dueTime[0].minutes == 0L) {
            viewHolder.due.text = viewHolder.itemView.resources.getString(R.string.live_data_due)
        } else {
            viewHolder.due.text = viewHolder.itemView.resources.getString(R.string.live_data_due_time, liveData.dueTime[0].minutes)
        }
    }

}
