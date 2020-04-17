package ie.dublinmapper.search

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.list_item_no_location.view.*

class NoLocationItem(
    private val onEnableLocationClickedListener: OnEnableLocationClickedListener,
    private val index: Int
) : Item() {

    override fun getLayout() = R.layout.list_item_no_location

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.search_button_enable_location.setOnClickListener {
            onEnableLocationClickedListener.onEnableLocationClicked()
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other is NoLocationItem) {
            return other.index == index
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        return other is NoLocationItem
    }

    override fun hashCode(): Int {
        return NoLocationItem::class.java.simpleName.hashCode()
    }
}

interface OnEnableLocationClickedListener {
    fun onEnableLocationClicked()
}
