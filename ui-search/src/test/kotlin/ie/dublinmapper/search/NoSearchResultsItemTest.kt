package ie.dublinmapper.search

import ie.dublinmapper.model.AbstractUniqueItemTest

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
