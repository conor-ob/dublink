package ie.dublinmapper.model.aircoach

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.AircoachLiveData
import kotlinx.android.synthetic.main.list_item_live_data_aircoach.*


class AircoachLiveDataItem(
    private val liveData: AircoachLiveData
) : Item() {

    override fun getLayout() = R.layout.list_item_live_data_aircoach

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.route_id.text = liveData.route
        viewHolder.destination.text = liveData.destination
        if (liveData.dueTime[0].minutes == 0L) {
            viewHolder.due.text = viewHolder.itemView.resources.getString(R.string.live_data_due)
        } else {
            viewHolder.due.text = viewHolder.itemView.resources.getString(R.string.live_data_due_time, liveData.dueTime[0].minutes)
        }
    }

}
