package io.dublink.model

class DividerItemTest : AbstractCommonItemTest<DividerItem>() {

    override val item1: DividerItem
        get() = DividerItem(id = 1L)

    override val item2: DividerItem
        get() = DividerItem(id = 2L)
}
