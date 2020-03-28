package ie.dublinmapper.model.dublinbus

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.domain.model.getName
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.DublinBusStop
import io.rtpi.api.Service

class DublinBusStopItem(
    dublinBusStop: DublinBusStop,
    distance: Double?
) : ServiceLocationItem(dublinBusStop, distance) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)
        bindTitle(
            viewHolder,
            getServiceLocation().getName(),
            "${Service.DUBLIN_BUS.fullName} (${getServiceLocation().id})"
        )
        bindIcon(viewHolder, R.drawable.ic_bus, R.color.dublinBusYellow)
        bindRoutes(viewHolder, (getServiceLocation() as DublinBusStop).routes)
    }

}
