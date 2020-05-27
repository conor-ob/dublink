package io.dublink.search

import io.dublink.test.AbstractCommonItemTest
import io.mockk.mockk
import org.junit.Test

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

    @Test
    override fun ignored() { }
}
