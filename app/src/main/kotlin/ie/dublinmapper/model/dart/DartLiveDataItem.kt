package ie.dublinmapper.model.dart

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.DartLiveData
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.StringUtils
import kotlinx.android.synthetic.main.list_item_live_data_dart.*
import kotlinx.android.synthetic.main.list_item_live_data_dart.direction_destination
import kotlinx.android.synthetic.main.list_item_live_data_dart.due
import kotlinx.android.synthetic.main.list_item_live_data_dart.train_type
import kotlinx.android.synthetic.main.list_item_live_data_dart_condensed.*
import java.util.*

abstract class AbstractDartLiveDataItem(
    private val liveData: DartLiveData
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.train_type.text = liveData.operator.fullName
        when (liveData.operator) {
            Operator.DART -> viewHolder.train_type.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.dartGreen))
            Operator.COMMUTER -> viewHolder.train_type.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.commuterBlue))
            Operator.INTERCITY -> viewHolder.train_type.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.intercityGrey))
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
    }

}

class DartLiveDataItem(
    liveData: DartLiveData
) : AbstractDartLiveDataItem(liveData) {

    override fun getLayout() = R.layout.list_item_live_data_dart

}

class DartLiveDataItemEnd(
    liveData: DartLiveData
) : AbstractDartLiveDataItem(liveData) {

    override fun getLayout() = R.layout.list_item_live_data_dart_end

}
