package ie.dublinmapper.model.dublinbikes

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.DublinBikesDock

class DublinBikesDockItem(
    dublinBikesDock: DublinBikesDock,
    isEven: Boolean,
    isLast: Boolean
) : ServiceLocationItem(dublinBikesDock, isEven, isLast) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        bindBackground(viewHolder, position)
        bindTitle(
            viewHolder,
            getDublinBikesDock().name,
            "Bikes: ${getDublinBikesDock().availableBikes}," + "Docks: ${getDublinBikesDock().availableDocks}"
        )
        bindIcon(viewHolder, R.drawable.ic_bike, R.color.dublinBikesTeal)
    }

    private fun getDublinBikesDock(): DublinBikesDock {
        return getServiceLocation() as DublinBikesDock
    }

}
