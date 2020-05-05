package io.dublink.search

import io.dublink.model.AbstractCommonItemTest
import io.mockk.mockk

class ClearRecentSearchesItemTest : AbstractCommonItemTest<ClearRecentSearchesItem>() {

    override val item1: ClearRecentSearchesItem
        get() = ClearRecentSearchesItem(
            id = 1L,
            clickListener = mockk()
        )

    override val item2: ClearRecentSearchesItem
        get() = ClearRecentSearchesItem(
            id = 2L,
            clickListener = mockk()
        )
}