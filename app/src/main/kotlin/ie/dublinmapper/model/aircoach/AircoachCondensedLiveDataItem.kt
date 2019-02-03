package ie.dublinmapper.model.aircoach

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.AircoachLiveData
import ie.dublinmapper.util.StringUtils
import kotlinx.android.synthetic.main.view_nearby_list_item_aircoach_condensed.*

class AircoachCondensedLiveDataItem(
    private val liveData: AircoachLiveData
) : Item() {

    override fun getLayout() = R.layout.view_nearby_list_item_aircoach_condensed

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.route_id.text = liveData.route
        viewHolder.destination.text = liveData.destination
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