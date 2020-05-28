package io.dublink.livedata

import io.dublink.test.AbstractCommonItemTest
import org.junit.Test

class NoLiveDataItemTest : AbstractCommonItemTest<NoLiveDataItem>() {

    override val item1: NoLiveDataItem
        get() = NoLiveDataItem(message = "1", id = 1L)

    override val item2: NoLiveDataItem
        get() = NoLiveDataItem(message = "2", id = 2L)

    @Test
    override fun ignored() { }
}
