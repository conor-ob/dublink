package ie.dublinmapper.model.aircoach

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.AircoachStop

class AircoachStopItem(
    aircoachStop: AircoachStop,
    isEven: Boolean,
    isLast: Boolean
) : ServiceLocationItem(aircoachStop, isEven, isLast) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        bindBackground(viewHolder, position)
        bindTitle(viewHolder, getServiceLocation().name, null)
        bindIcon(viewHolder, R.drawable.ic_bus, R.color.aircoachOrange)
    }

}
