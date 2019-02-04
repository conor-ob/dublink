package ie.dublinmapper.model.dublinbikes

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.DublinBikesLiveData
import kotlinx.android.synthetic.main.list_item_live_data_dublin_bikes.*

class DublinBikesLiveDataItem(
    private val liveData: DublinBikesLiveData
) : Item() {

    override fun getLayout() = R.layout.list_item_live_data_dublin_bikes

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.available_bikes.text = liveData.bikes.toString()
        viewHolder.available_docks.text = liveData.docks.toString()
    }

}
