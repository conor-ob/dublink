package ie.dublinmapper.model.irishrail

import com.xwray.groupie.Item
import ie.dublinmapper.model.LiveDataItem
import io.rtpi.api.IrishRailLiveData

open class IrishRailLiveDataItem(
    liveData: IrishRailLiveData
) : LiveDataItem(liveData) {

    override fun isSameAs(other: Item<*>): Boolean {
        if (other is IrishRailLiveDataItem) {
            return liveData.operator == other.liveData.operator &&
                    liveData.route == other.liveData.route &&
                    liveData.destination == other.liveData.destination &&
                    liveData.direction == other.liveData.direction
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is IrishRailLiveDataItem) {
            return liveData == other.liveData
        }
        return false
    }

    override fun hashCode(): Int {
        return liveData.hashCode()
    }

}
