package ie.dublinmapper.model.buseireann

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.BusEireannStop
import ie.dublinmapper.model.BusEireannStopUi
import kotlinx.android.synthetic.main.list_item_service_location_bus_eireann.*

class BusEireannStopItem(private val busEireannStop: BusEireannStopUi) : Item() {

    init {
        extras["serviceLocation"] = busEireannStop
    }

    override fun getLayout() = R.layout.list_item_service_location_bus_eireann

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.service_location_name.text = busEireannStop.name
    }

}
