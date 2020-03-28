package ie.dublinmapper.model.irishrail

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.core.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.IrishRailStation
import io.rtpi.api.Route
import io.rtpi.api.Service

class IrishRailStationItem(
    irishRailStation: IrishRailStation,
    distance: Double?
) : ServiceLocationItem(irishRailStation, distance) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        bindTitle(viewHolder, getServiceLocation().name, Service.IRISH_RAIL.fullName)
        bindIcon(viewHolder, R.drawable.ic_train, R.color.dartGreen)
        bindRoutes(viewHolder, (getServiceLocation() as IrishRailStation).operators.map { Route(it.fullName, it) })
    }

}