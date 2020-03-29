package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ie.dublinmapper.ui.R

class DividerItem : Item() {

    override fun getLayout() = R.layout.list_item_live_data_divider

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        return equals(other)
    }

    override fun equals(other: Any?): Boolean {
        return other is DividerItem
    }

    override fun hashCode(): Int {
        return id.toInt()
    }

}
