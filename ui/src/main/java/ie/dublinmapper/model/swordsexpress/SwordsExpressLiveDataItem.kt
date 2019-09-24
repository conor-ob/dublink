package ie.dublinmapper.model.swordsexpress

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.ui.R
import ie.dublinmapper.domain.model.SwordsExpressLiveData

class SwordsExpressLiveDataItem(
    private val swordsExpressLiveData: SwordsExpressLiveData
) : Item() {

    override fun getLayout() = R.layout.list_item_live_data

    override fun bind(viewHolder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
