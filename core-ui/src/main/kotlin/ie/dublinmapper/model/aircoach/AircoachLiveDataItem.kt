package ie.dublinmapper.model.aircoach

import com.xwray.groupie.Item
import ie.dublinmapper.model.LiveDataItem
import io.rtpi.api.AircoachLiveData

open class AircoachLiveDataItem(
    liveData: AircoachLiveData
) : LiveDataItem(liveData) {

    override fun isSameAs(other: Item<*>?): Boolean {
        if (other is AircoachLiveDataItem) {
            return liveData.operator == other.liveData.operator &&
                    liveData.route == other.liveData.route &&
                    liveData.destination == other.liveData.destination
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is AircoachLiveDataItem) {
            return liveData == other.liveData
        }
        return false
    }

    override fun hashCode(): Int {
        return liveData.hashCode()
    }

}
