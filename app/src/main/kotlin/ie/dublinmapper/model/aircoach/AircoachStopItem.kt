package ie.dublinmapper.model.aircoach

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.AircoachStop
import kotlinx.android.synthetic.main.list_item_service_location_aircoach.*

class AircoachStopItem(private val aircoachStop: AircoachStop) : Item() {

    override fun getLayout() = R.layout.list_item_service_location_aircoach

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.service_location_name.text = aircoachStop.name
    }

}
