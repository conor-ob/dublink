package ie.dublinmapper.model.dublinbus

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.DublinBusStop
import ie.dublinmapper.model.DublinBusStopUi
import kotlinx.android.synthetic.main.list_item_service_location_dublin_bus.*

class DublinBusStopItem(private val dublinBusStop: DublinBusStopUi) : Item() {

    init {
        extras["serviceLocation"] = dublinBusStop
    }

    override fun getLayout() = R.layout.list_item_service_location_dublin_bus

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.service_location_name.text = dublinBusStop.name
    }

}
