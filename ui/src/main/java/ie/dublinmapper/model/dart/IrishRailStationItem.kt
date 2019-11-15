package ie.dublinmapper.model.dart

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.IrishRailStation
import io.rtpi.api.Operator
import io.rtpi.api.Route
import io.rtpi.api.Service

class IrishRailStationItem(
    dartStation: IrishRailStation
) : ServiceLocationItem(dartStation) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        bindTitle(viewHolder, getServiceLocation().name, Service.IRISH_RAIL.fullName)
        bindIcon(viewHolder, R.drawable.ic_train, R.color.dartGreen)
        bindRoutes(viewHolder, (getServiceLocation() as IrishRailStation).operators.map { Route(it.fullName, it) })
    }

}
