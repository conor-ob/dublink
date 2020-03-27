package ie.dublinmapper.model.buseireann

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.core.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.BusEireannStop
import io.rtpi.api.Service

class BusEireannStopItem(
    busEireannStop: BusEireannStop,
    distance: Double?
) : ServiceLocationItem(busEireannStop, distance) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        bindTitle(
            viewHolder,
            getServiceLocation().name,
            "${Service.BUS_EIREANN.fullName} (${getServiceLocation().id})"
        )
        bindIcon(viewHolder, R.drawable.ic_bus, R.color.busEireannRed)
        bindRoutes(viewHolder, (getServiceLocation() as BusEireannStop).routes)
    }

}
