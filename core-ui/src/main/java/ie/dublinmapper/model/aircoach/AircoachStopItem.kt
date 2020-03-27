package ie.dublinmapper.model.aircoach

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.core.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.AircoachStop
import io.rtpi.api.Service

class AircoachStopItem(
    aircoachStop: AircoachStop,
    distance: Double?
) : ServiceLocationItem(aircoachStop, distance) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        bindTitle(viewHolder, getServiceLocation().name, Service.AIRCOACH.fullName)
        bindIcon(viewHolder, R.drawable.ic_bus, R.color.aircoachOrange)
        bindRoutes(viewHolder, (getServiceLocation() as AircoachStop).routes)
    }

}
