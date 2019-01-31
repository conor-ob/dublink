package ie.dublinmapper.model

import ie.dublinmapper.domain.model.*

sealed class LiveDataUi

class DartLiveDataHeaderUi(
    val liveData: DartLiveDataHeader
) : LiveDataUi()

class DartLiveDataUi(
    val liveData: DartLiveData
) : LiveDataUi() {
    override fun toString(): String {
        return liveData.toString()
    }
}

class DublinBikesLiveDataUi(
    val liveData: DublinBikesLiveData
) : LiveDataUi() {
    override fun toString(): String {
        return liveData.toString()
    }
}

class DublinBusLiveDataUi(
    val liveData: DublinBusLiveData
) : LiveDataUi() {
    override fun toString(): String {
        return liveData.toString()
    }
}

class LuasLiveDataUi(
    val liveData: LuasLiveData
) : LiveDataUi() {
    override fun toString(): String {
        return liveData.toString()
    }
}
