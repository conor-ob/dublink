package ie.dublinmapper.livedata

import ie.dublinmapper.model.AbstractCommonItemTest

class NoLiveDataItemTest : AbstractCommonItemTest<NoLiveDataItem>() {

    override val item1: NoLiveDataItem
        get() = NoLiveDataItem(id = 1L)

    override val item2: NoLiveDataItem
        get() = NoLiveDataItem(id = 2L)
}
