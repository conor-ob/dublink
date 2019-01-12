package ie.dublinmapper.model

import ie.dublinmapper.domain.model.LiveData

sealed class LiveDataUi {

    class Header(
        val serviceLocation: ServiceLocationUi
    ) : LiveDataUi()

    class Dart(
        val liveData: LiveData.Dart
    ) : LiveDataUi()

    class DublinBikes(
        val liveData: LiveData.DublinBikes
    ) : LiveDataUi()

    class DublinBus(
        val liveData: LiveData.DublinBus
    ) : LiveDataUi()

    class Luas(
        val liveData: LiveData.Luas
    ) : LiveDataUi()

}
