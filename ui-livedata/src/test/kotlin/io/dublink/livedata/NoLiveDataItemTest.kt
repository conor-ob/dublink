package io.dublink.livedata

import io.dublink.model.AbstractCommonItemTest
import io.rtpi.api.Service

class NoLiveDataItemTest : AbstractCommonItemTest<NoLiveDataItem>() {

    override val item1: NoLiveDataItem
        get() = NoLiveDataItem(service = Service.DUBLIN_BUS, id = 1L)

    override val item2: NoLiveDataItem
        get() = NoLiveDataItem(service = Service.DUBLIN_BIKES, id = 2L)
}
