package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.ui.R

class DividerItem(private val index: Int) : Item() {

    override fun getLayout() = R.layout.list_item_live_data_divider

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // nothing to do
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        return equals(other)
    }

    override fun equals(other: Any?): Boolean {
        if (other is DividerItem) {
            return other.index == index
        }
        return false
    }

    override fun hashCode(): Int {
        return index.hashCode()
    }
}
