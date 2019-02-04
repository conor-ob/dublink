package ie.dublinmapper.model.luas

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.LuasStop
import ie.dublinmapper.util.Operator
import kotlinx.android.synthetic.main.list_item_service_location_luas.*

class LuasStopItem(private val luasStop: LuasStop) : Item() {

    override fun getLayout() = R.layout.list_item_service_location_luas

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.service_location_name.text = luasStop.name
        val lines = luasStop.routes[Operator.LUAS]
    }

}
