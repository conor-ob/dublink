package io.dublink.search

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.dublink.model.AbstractCommonItem
import kotlinx.android.synthetic.main.list_item_search_clear_recent_searches.view.*

class ClearRecentSearchesItem(
    id: Long,
    private val clickListener: ClearRecentSearchesClickListener
) : AbstractCommonItem(id) {

    override fun getLayout() = R.layout.list_item_search_clear_recent_searches

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.search_button_clear_recent_searches.setOnClickListener {
            clickListener.onClearRecentSearchesClicked()
        }
    }
}

interface ClearRecentSearchesClickListener {
    fun onClearRecentSearchesClicked()
}
