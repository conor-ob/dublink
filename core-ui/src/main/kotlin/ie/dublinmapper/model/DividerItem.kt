package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import ie.dublinmapper.core.R

class DividerItem : Item() {

    override fun getLayout() = R.layout.list_item_live_data_divider

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other is DividerItem) {
            return id == other.id
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is DividerItem) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id.toInt()
    }

}
