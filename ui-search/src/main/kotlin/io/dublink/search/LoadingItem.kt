package io.dublink.search

import io.dublink.model.AbstractCommonItem
import io.dublink.search.R

class LoadingItem(id: Long) : AbstractCommonItem(id) {

    override fun getLayout() =
        R.layout.list_item_search_location_loading
}
