package ie.dublinmapper.model.luas

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.model.ServiceLocationItem
import kotlinx.android.synthetic.main.list_item_service_location.*

class LuasStopItem(
    val luasStop: LuasStop,
    isEven: Boolean,
    isLast: Boolean
) : ServiceLocationItem(isEven, isLast) {

    init {
        extras["serviceLocation"] = luasStop
    }

    override fun getLayout() = R.layout.list_item_service_location

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.title.text = luasStop.name
        viewHolder.subtitle.text = luasStop.id
        viewHolder.serviceIconContainer.setImageResource(R.drawable.ic_tram)
        viewHolder.serviceIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.itemView.context, R.color.luasPurple))
    }

}