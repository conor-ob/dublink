package io.dublink.model

import io.dublink.test.AbstractCommonItemTest
import org.junit.Test

class DividerItemTest : AbstractCommonItemTest<DividerItem>() {

    override val item1: DividerItem
        get() = DividerItem(id = 1L)

    override val item2: DividerItem
        get() = DividerItem(id = 2L)

    @Test
    override fun ignored() { }
}
