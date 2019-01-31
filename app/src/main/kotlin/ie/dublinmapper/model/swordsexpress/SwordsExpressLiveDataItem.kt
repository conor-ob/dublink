package ie.dublinmapper.model.swordsexpress

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R
import ie.dublinmapper.domain.model.SwordsExpressLiveData

class SwordsExpressLiveDataItem(
    private val swordsExpressLiveData: SwordsExpressLiveData
) : Item() {

    override fun getLayout() = R.layout.view_nearby_list_item_swords_express

    override fun bind(viewHolder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
