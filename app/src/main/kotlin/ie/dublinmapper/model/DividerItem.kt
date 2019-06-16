package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R

class DividerItem : Item() {

    override fun getLayout() = R.layout.list_item_live_data_divider

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

}
