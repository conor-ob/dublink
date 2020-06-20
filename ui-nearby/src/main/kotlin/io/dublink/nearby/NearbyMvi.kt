package io.dublink.nearby

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import io.dublink.domain.model.DubLinkServiceLocation
import io.rtpi.api.Coordinate
import io.rtpi.api.LiveData

sealed class Action : BaseAction {
    data class OnMapMoveFinished(val coordinate: Coordinate) : Action()
    data class GetLiveData(val serviceLocation: DubLinkServiceLocation) : Action()
}

sealed class NewState {
    data class NearbyServiceLocations(val serviceLocations: List<DubLinkServiceLocation>) : NewState()
    data class GetLiveData(val serviceLocation: DubLinkServiceLocation, val liveData: List<LiveData>?) : NewState()
}

data class State(
    val nearbyServiceLocations: List<DubLinkServiceLocation>,
    val focusedServiceLocation: DubLinkServiceLocation?,
    val focusedServiceLocationLiveData: List<LiveData>?
) : BaseState
