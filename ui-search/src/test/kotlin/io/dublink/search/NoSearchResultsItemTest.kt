package io.dublink.search

import io.dublink.test.AbstractUniqueItemTest
import org.junit.Test

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

    @Test
    override fun ignored() { }
}
