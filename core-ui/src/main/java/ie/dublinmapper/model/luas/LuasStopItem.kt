package ie.dublinmapper.model.luas

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.core.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.LuasStop
import io.rtpi.api.Service

class LuasStopItem(
    luasStop: LuasStop,
    distance: Double?
) : ServiceLocationItem(luasStop, distance) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        bindTitle(viewHolder, getServiceLocation().name, Service.LUAS.fullName)
        bindIcon(viewHolder, R.drawable.ic_tram, R.color.luasPurple)
        bindRoutes(viewHolder, (getServiceLocation() as LuasStop).routes)
    }

}
