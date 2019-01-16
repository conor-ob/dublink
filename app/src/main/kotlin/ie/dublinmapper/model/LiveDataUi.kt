package ie.dublinmapper.model

import ie.dublinmapper.domain.model.LiveData

sealed class LiveDataUi {

    class DartHeader(
        val liveData: LiveData.DartHeader
    ) : LiveDataUi()

    class Dart(
        val liveData: LiveData.Dart
    ) : LiveDataUi() {
        override fun toString(): String {
            return liveData.toString()
        }
    }

    class DublinBikes(
        val liveData: LiveData.DublinBikes
    ) : LiveDataUi() {
        override fun toString(): String {
            return liveData.toString()
        }
    }

    class DublinBus(
        val liveData: LiveData.DublinBus
    ) : LiveDataUi() {
        override fun toString(): String {
            return liveData.toString()
        }
    }

    class Luas(
        val liveData: LiveData.Luas
    ) : LiveDataUi() {
        override fun toString(): String {
            return liveData.toString()
        }
    }

}
