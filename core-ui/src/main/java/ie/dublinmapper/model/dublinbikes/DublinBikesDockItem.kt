package ie.dublinmapper.model.dublinbikes

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.DublinBikesDock
import io.rtpi.api.Operator
import io.rtpi.api.Route
import io.rtpi.api.Service

class DublinBikesDockItem(
    dublinBikesDock: DublinBikesDock,
    distance: Double?
) : ServiceLocationItem(dublinBikesDock, distance) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        bindTitle(
            viewHolder,
            getDublinBikesDock().name,
            Service.DUBLIN_BIKES.fullName
        )
        bindIcon(viewHolder, R.drawable.ic_bike, R.color.dublinBikesTeal)
        bindRoutes(viewHolder, listOf(
            Route("${getDublinBikesDock().availableBikes} Bikes", Operator.DUBLIN_BIKES),
            Route("${getDublinBikesDock().availableDocks} Docks", Operator.DUBLIN_BIKES)
        ))
    }

    private fun getDublinBikesDock(): DublinBikesDock {
        return getServiceLocation() as DublinBikesDock
    }

}
