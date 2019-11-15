package ie.dublinmapper.model.dublinbikes

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.DublinBikesDock

class DublinBikesDockItem(
    dublinBikesDock: DublinBikesDock
) : ServiceLocationItem(dublinBikesDock) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        bindTitle(
            viewHolder,
            getDublinBikesDock().name,
            "Bikes: ${getDublinBikesDock().availableBikes}," + "Docks: ${getDublinBikesDock().availableDocks}"
        )
        bindIcon(viewHolder, R.drawable.ic_bike, R.color.dublinBikesTeal)
        bindRoutes(viewHolder, emptyList())
    }

    private fun getDublinBikesDock(): DublinBikesDock {
        return getServiceLocation() as DublinBikesDock
    }

}
