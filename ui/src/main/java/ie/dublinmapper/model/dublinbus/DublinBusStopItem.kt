package ie.dublinmapper.model.dublinbus

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.DublinBusStop

class DublinBusStopItem(
    dublinBusStop: DublinBusStop,
    isEven: Boolean,
    isLast: Boolean
) : ServiceLocationItem(dublinBusStop, isEven, isLast) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        bindBackground(viewHolder, position)
        bindTitle(
            viewHolder,
            getServiceLocation().name,
            viewHolder.itemView.context.getString(R.string.stop_number, getServiceLocation().id)
        )
        bindIcon(viewHolder, R.drawable.ic_bus, R.color.dublinBusYellow)
        bindRoutes(viewHolder, (getServiceLocation() as DublinBusStop).routes.map { it.id })
    }

}
