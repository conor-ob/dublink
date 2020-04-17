package ie.dublinmapper.search

import ie.dublinmapper.model.AbstractCommonItemTest

class NoRecentSearchesItemTest : AbstractCommonItemTest<NoRecentSearchesItem>() {

    override val item1: NoRecentSearchesItem
        get() = NoRecentSearchesItem(id = 1L)

    override val item2: NoRecentSearchesItem
        get() = NoRecentSearchesItem(id = 2L)
}
