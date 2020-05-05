package io.dublink.search

import io.dublink.model.AbstractCommonItemTest

class NoRecentSearchesItemTest : AbstractCommonItemTest<NoRecentSearchesItem>() {

    override val item1: NoRecentSearchesItem
        get() = NoRecentSearchesItem(id = 1L)

    override val item2: NoRecentSearchesItem
        get() = NoRecentSearchesItem(id = 2L)
}
