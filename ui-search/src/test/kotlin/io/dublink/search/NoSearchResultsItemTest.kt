package io.dublink.search

import io.dublink.model.AbstractUniqueItemTest
import io.dublink.search.NoSearchResultsItem

class NoSearchResultsItemTest : AbstractUniqueItemTest<NoSearchResultsItem>() {

    override val controlItem: NoSearchResultsItem
        get() = NoSearchResultsItem(
            id = 1L,
            query = "query1"
        )

    override val sameItemNotEqual: NoSearchResultsItem
        get() = NoSearchResultsItem(
            id = 2L,
            query = "query2"
        )

    override val equalItem: NoSearchResultsItem
        get() = NoSearchResultsItem(
            id = 1L,
            query = "query1"
        )
}
