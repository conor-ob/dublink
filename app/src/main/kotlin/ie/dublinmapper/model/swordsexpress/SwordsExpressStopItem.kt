package ie.dublinmapper.model.swordsexpress

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.SwordsExpressStop
import kotlinx.android.synthetic.main.list_item_service_location_swords_express.*

class SwordsExpressStopItem(private val swordsExpressStop: SwordsExpressStop) : Item() {

    override fun getLayout() = R.layout.list_item_service_location_swords_express

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.service_location_name.text = swordsExpressStop.name
    }

}
