package ie.dublinmapper.search

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class NoRecentSearchesItem : Item() {

    override fun getLayout() = R.layout.list_item_no_recent_searches

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // nothing to do
    }
}
