package ie.dublinmapper.search

import ie.dublinmapper.model.AbstractCommonItem

class NoRecentSearchesItem(
    id: Long
) : AbstractCommonItem(id) {

    override fun getLayout() = R.layout.list_item_no_recent_searches
}
