package ie.dublinmapper.search

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class NoLocationItem : Item() {

    override fun getLayout() = R.layout.list_item_no_location

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // nothing to do
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        return true
    }

    override fun equals(other: Any?): Boolean {
        return true
    }

    override fun hashCode(): Int {
        return 1
    }
}
