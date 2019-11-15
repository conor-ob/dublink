package ie.dublinmapper.model.luas

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.LuasStop
import io.rtpi.api.Service

class LuasStopItem(
    luasStop: LuasStop
) : ServiceLocationItem(luasStop) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        bindTitle(viewHolder, getServiceLocation().name, Service.LUAS.fullName)
        bindIcon(viewHolder, R.drawable.ic_tram, R.color.luasPurple)
        bindRoutes(viewHolder, (getServiceLocation() as LuasStop).routes)
    }

}
