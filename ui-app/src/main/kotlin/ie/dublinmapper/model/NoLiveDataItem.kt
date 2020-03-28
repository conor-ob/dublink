package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.ui.R
import kotlinx.android.synthetic.main.list_item_no_live_data.*

class NoLiveDataItem(private val message: String) : Item() {

    override fun getLayout() = R.layout.list_item_no_live_data

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.message.text = message
    }
}
