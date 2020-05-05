package io.dublink.search

import io.dublink.model.AbstractCommonItem

class NoRecentSearchesItem(
    id: Long
) : AbstractCommonItem(id) {

    override fun getLayout() = R.layout.list_item_no_recent_searches
}
