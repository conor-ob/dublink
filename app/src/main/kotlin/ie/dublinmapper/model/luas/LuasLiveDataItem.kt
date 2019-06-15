package ie.dublinmapper.model.luas

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.LuasLiveData
import ie.dublinmapper.model.LuasLiveDataUi
import ie.dublinmapper.util.StringUtils
import kotlinx.android.synthetic.main.list_item_live_data_luas.*
import java.util.*

class LuasLiveDataItem(
    private val liveData: LuasLiveDataUi
) : Item() {

    override fun getLayout() = R.layout.list_item_live_data_luas

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.operator.text = liveData.liveData.operator.name
        viewHolder.direction_destination.text = StringUtils.join(
            Arrays.asList(
                liveData.liveData.route,
                liveData.liveData.destination
            ), " ${StringUtils.MIDDLE_DOT} ")
        if (liveData.liveData.dueTime[0].minutes == 0L) {
            viewHolder.due.text = viewHolder.itemView.resources.getString(R.string.live_data_due)
        } else {
            viewHolder.due.text = viewHolder.itemView.resources.getString(R.string.live_data_due_time, liveData.liveData.dueTime[0].minutes)
        }
    }

}
