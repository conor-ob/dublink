package ie.dublinmapper.model.dublinbus

import com.xwray.groupie.Item
import ie.dublinmapper.model.LiveDataItem
import io.rtpi.api.DublinBusLiveData

class DublinBusLiveDataItem(
    liveData: DublinBusLiveData
) : LiveDataItem(liveData) {

    override fun isSameAs(other: Item<*>): Boolean {
        if (other is DublinBusLiveDataItem) {
            return liveData.operator == other.liveData.operator &&
                    liveData.route == other.liveData.route &&
                    liveData.destination == other.liveData.destination
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is DublinBusLiveDataItem) {
            return liveData == other.liveData
        }
        return false
    }

    override fun hashCode(): Int {
        return liveData.hashCode()
    }

}
