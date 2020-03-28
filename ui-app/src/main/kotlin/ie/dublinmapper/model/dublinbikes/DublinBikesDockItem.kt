package ie.dublinmapper.model.dublinbikes

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.domain.model.getName
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.DublinBikesDock
import io.rtpi.api.Operator
import io.rtpi.api.Route
import io.rtpi.api.Service

class DublinBikesDockItem(
    dublinBikesDock: DublinBikesDock,
    distance: Double?
) : ServiceLocationItem(dublinBikesDock, distance) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)
        bindTitle(
            viewHolder,
            getDublinBikesDock().getName(),
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
