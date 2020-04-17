package ie.dublinmapper.search

import ie.dublinmapper.model.AbstractCommonItemTest

class NoSearchResultsItemTest : AbstractCommonItemTest<NoSearchResultsItem>() {

    override val item1: NoSearchResultsItem
        get() = NoSearchResultsItem(
            id = 1L,
            query = "search"
        )

    override val item2: NoSearchResultsItem
        get() = NoSearchResultsItem(
            id = 2L,
            query = "search"
        )
}
