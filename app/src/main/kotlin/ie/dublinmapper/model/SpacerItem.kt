package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.R

class SpacerItem() : Item() {

    override fun getLayout() = R.layout.list_item_live_data_spacer

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

}
