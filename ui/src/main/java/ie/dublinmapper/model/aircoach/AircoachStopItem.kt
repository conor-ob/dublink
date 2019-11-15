package ie.dublinmapper.model.aircoach

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.AircoachStop
import io.rtpi.api.Operator
import io.rtpi.api.Service

class AircoachStopItem(
    aircoachStop: AircoachStop
) : ServiceLocationItem(aircoachStop) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        bindTitle(viewHolder, getServiceLocation().name, Service.AIRCOACH.fullName)
        bindIcon(viewHolder, R.drawable.ic_bus, R.color.aircoachOrange)
        bindRoutes(viewHolder, (getServiceLocation() as AircoachStop).routes)
    }

}
