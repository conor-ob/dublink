package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.ui.R

class LoadingItem : Item() {

    override fun getLayout() = R.layout.list_item_progress_bar

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // nothing to do
    }
}
