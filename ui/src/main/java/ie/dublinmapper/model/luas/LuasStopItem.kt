package ie.dublinmapper.model.luas

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.model.ServiceLocationItem
import io.rtpi.api.LuasStop

class LuasStopItem(
    luasStop: LuasStop,
    isEven: Boolean,
    isLast: Boolean
) : ServiceLocationItem(luasStop, isEven, isLast) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        bindBackground(viewHolder, position)
        bindTitle(viewHolder, getServiceLocation().name, null)
        bindIcon(viewHolder, R.drawable.ic_tram, R.color.luasPurple)
        bindRoutes(viewHolder, (getServiceLocation() as LuasStop).routes.values.flatten())
    }

}
