package ie.dublinmapper.search

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.list_item_search_clear_recent_searches.view.*

class ClearRecentSearchesItem(
    private val onClearRecentSearchesClickedListener: OnClearRecentSearchesClickedListener,
    private val index: Int
) : Item() {

    override fun getLayout() = R.layout.list_item_search_clear_recent_searches

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.search_button_clear_recent_searches.setOnClickListener {
            onClearRecentSearchesClickedListener.onClearRecentSearchesClicked()
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other is ClearRecentSearchesItem) {
            return other.index == index
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        return other is ClearRecentSearchesItem
    }

    override fun hashCode(): Int {
        return ClearRecentSearchesItem::class.java.simpleName.hashCode()
    }
}

interface OnClearRecentSearchesClickedListener {
    fun onClearRecentSearchesClicked()
}
