package ie.dublinmapper.livedata

import ie.dublinmapper.model.AbstractCommonItem
import ie.dublinmapper.ui.R

class NoLiveDataItem(id: Long) : AbstractCommonItem(id) {

    override fun getLayout() = R.layout.list_item_no_live_data
}
