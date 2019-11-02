package ie.dublinmapper.model.aircoach

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import io.rtpi.api.AircoachLiveData
import kotlinx.android.synthetic.main.list_item_live_data.*

class TerminatingAircoachLiveDataItem(
    private val liveData: AircoachLiveData,
    isEven: Boolean,
    isLast: Boolean
) : AircoachLiveDataItem(liveData, isEven, isLast) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.title.text = liveData.route + " " + String.format(viewHolder.itemView.resources.getString(R.string.from), liveData.origin)
    }
}