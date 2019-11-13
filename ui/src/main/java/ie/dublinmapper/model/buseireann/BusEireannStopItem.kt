package ie.dublinmapper.model.buseireann

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.BusEireannStop

class BusEireannStopItem(
    busEireannStop: BusEireannStop,
    isEven: Boolean,
    isLast: Boolean
) : ServiceLocationItem(busEireannStop, isEven, isLast) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        bindBackground(viewHolder, position)
        bindTitle(
            viewHolder,
            getServiceLocation().name,
            viewHolder.itemView.context.getString(R.string.stop_number, getServiceLocation().id)
        )
        bindIcon(viewHolder, R.drawable.ic_bus, R.color.busEireannRed)
        bindRoutes(viewHolder, (getServiceLocation() as BusEireannStop).routes.map { it.id })
    }

}
