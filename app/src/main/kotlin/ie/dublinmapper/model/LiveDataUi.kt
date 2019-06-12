package ie.dublinmapper.model

import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.domain.model.*
import ie.dublinmapper.model.aircoach.AircoachCondensedLiveDataItem
import ie.dublinmapper.model.aircoach.AircoachLiveDataItem
import ie.dublinmapper.model.dart.DartCondensedLiveDataItem
import ie.dublinmapper.model.dart.DartCondensedLiveDataItemEnd
import ie.dublinmapper.model.dart.DartLiveDataItem
import ie.dublinmapper.model.dart.DartLiveDataItemEnd
import ie.dublinmapper.model.dublinbikes.DublinBikesLiveDataItem
import ie.dublinmapper.model.dublinbus.DublinBusCondensedLiveDataItem
import ie.dublinmapper.model.dublinbus.DublinBusLiveDataItem
import ie.dublinmapper.model.luas.LuasCondensedLiveDataItem
import ie.dublinmapper.model.luas.LuasLiveDataItem
import ie.dublinmapper.model.swordsexpress.SwordsExpressLiveDataItem

sealed class LiveDataUi {
    abstract fun toItem(isLast: Boolean = false): Item
}

class AircoachLiveDataUi(
    val liveData: AircoachLiveData
) : LiveDataUi() {
    override fun toString() = liveData.toString()
    override fun toItem(isLast: Boolean) = if (liveData.dueTime.size > 1) {
        AircoachCondensedLiveDataItem(liveData)
    } else {
        AircoachLiveDataItem(liveData)
    }
}

class DartLiveDataUi(
    val liveData: DartLiveData
) : LiveDataUi() {
    override fun toString() = liveData.toString()

    override fun toItem(isLast: Boolean) = if (liveData.dueTime.size > 1) {
        if (isLast) {
            DartCondensedLiveDataItemEnd(liveData)
        } else {
            DartCondensedLiveDataItem(liveData)
        }
    } else {
        if (isLast) {
            DartLiveDataItemEnd(liveData)
        } else {
            DartLiveDataItem(liveData)
        }
    }

}

class DublinBikesLiveDataUi(
    val liveData: DublinBikesLiveData
) : LiveDataUi() {
    override fun toString() = liveData.toString()
    override fun toItem(isLast: Boolean) = DublinBikesLiveDataItem(liveData)
}

class DublinBusLiveDataUi(
    val liveData: DublinBusLiveData
) : LiveDataUi() {
    override fun toString() = liveData.toString()
    override fun toItem(isLast: Boolean) = if (liveData.dueTime.size > 1) {
        DublinBusCondensedLiveDataItem(liveData)
    } else {
        DublinBusLiveDataItem(liveData)
    }
}

class LuasLiveDataUi(
    val liveData: LuasLiveData
) : LiveDataUi() {
    override fun toString() = liveData.toString()
    override fun toItem(isLast: Boolean) = if (liveData.dueTime.size > 1) {
        LuasCondensedLiveDataItem(liveData)
    } else {
        LuasLiveDataItem(liveData)
    }
}

class SwordsExpressLiveDataUi(
    val liveData: SwordsExpressLiveData
) : LiveDataUi() {
    override fun toString() = liveData.toString()
    override fun toItem(isLast: Boolean) = SwordsExpressLiveDataItem(liveData)
}
