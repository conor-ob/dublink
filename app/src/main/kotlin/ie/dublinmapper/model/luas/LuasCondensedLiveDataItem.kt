package ie.dublinmapper.model.luas

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.LuasLiveData
import ie.dublinmapper.util.StringUtils
import kotlinx.android.synthetic.main.view_nearby_list_item_luas_condensed.*
import java.util.*

class LuasCondensedLiveDataItem(
    private val liveData: LuasLiveData
) : Item() {

    override fun getLayout() = R.layout.view_nearby_list_item_luas_condensed

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.operator.text = liveData.operator.name
        viewHolder.direction_destination.text = StringUtils.join(
            Arrays.asList(
                liveData.route,
                liveData.destination
            ), " ${StringUtils.MIDDLE_DOT} ")
        if (liveData.dueTime[0].minutes == 0L) {
            viewHolder.due.text = viewHolder.itemView.resources.getString(R.string.live_data_due)
        } else {
            viewHolder.due.text = viewHolder.itemView.resources.getString(R.string.live_data_due_time, liveData.dueTime[0].minutes)
        }
        viewHolder.due_later.text = viewHolder.itemView.resources.getString(R.string.live_data_due_times, StringUtils.join(
            liveData.dueTime.subList(1, liveData.dueTime.size).map { it.minutes.toString() }, ", "
        ))
    }

}
