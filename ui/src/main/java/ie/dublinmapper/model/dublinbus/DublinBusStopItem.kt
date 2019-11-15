package ie.dublinmapper.model.dublinbus

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.DublinBusStop
import io.rtpi.api.Service

class DublinBusStopItem(
    dublinBusStop: DublinBusStop
) : ServiceLocationItem(dublinBusStop) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        bindTitle(
            viewHolder,
            getServiceLocation().name,
            "${Service.DUBLIN_BUS.fullName} (${getServiceLocation().id})"
        )
        bindIcon(viewHolder, R.drawable.ic_bus, R.color.dublinBusYellow)
        bindRoutes(viewHolder, (getServiceLocation() as DublinBusStop).routes)
    }

}
