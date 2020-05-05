package ie.dublinmapper.favourites

import ie.dublinmapper.model.AbstractCommonItem

class NoFavouritesItem(id: Long) : AbstractCommonItem(id) {

    override fun getLayout() = R.layout.list_item_no_favourites
}
