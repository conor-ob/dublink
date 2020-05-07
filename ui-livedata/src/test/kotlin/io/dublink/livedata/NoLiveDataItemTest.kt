package io.dublink.livedata

import io.dublink.model.AbstractCommonItemTest

class NoLiveDataItemTest : AbstractCommonItemTest<NoLiveDataItem>() {

    override val item1: NoLiveDataItem
        get() = NoLiveDataItem(message = "1", id = 1L)

    override val item2: NoLiveDataItem
        get() = NoLiveDataItem(message = "2", id = 2L)
}
