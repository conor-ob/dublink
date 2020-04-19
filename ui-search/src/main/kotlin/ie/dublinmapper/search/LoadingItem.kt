package ie.dublinmapper.search

import ie.dublinmapper.model.AbstractCommonItem

class LoadingItem(id: Long) : AbstractCommonItem(id) {

    override fun getLayout() = R.layout.list_item_search_location_loading
}
