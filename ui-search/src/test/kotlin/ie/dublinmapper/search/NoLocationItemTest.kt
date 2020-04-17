package ie.dublinmapper.search

import ie.dublinmapper.model.AbstractCommonItemTest
import io.mockk.mockk

class NoLocationItemTest : AbstractCommonItemTest<NoLocationItem>() {

    override val item1: NoLocationItem
        get() = NoLocationItem(
            id = 1L,
            clickListener = mockk()
        )

    override val item2: NoLocationItem
        get() = NoLocationItem(
            id = 2L,
            clickListener = mockk()
        )
}
