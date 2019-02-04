package ie.dublinmapper.model.dublinbikes

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.DublinBikesDock
import kotlinx.android.synthetic.main.list_item_live_data_dublin_bikes.*

class DublinBikesDockItem(
    private val dublinBikesDock: DublinBikesDock
) : Item() {

    override fun getLayout() = R.layout.list_item_live_data_dublin_bikes

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.available_bikes.text = dublinBikesDock.availableBikes.toString()
        viewHolder.available_docks.text = "?"
    }

}
