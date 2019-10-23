package ie.dublinmapper.nearby

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.xwray.groupie.Group
import ie.dublinmapper.domain.model.DetailedServiceLocation
import io.rtpi.api.Coordinate

sealed class Action : BaseAction {
    object GetNearbyServiceLocations : Action()
}

sealed class Change {
    object Loading : Change()
    data class GetNearbyServiceLocations(val serviceLocations: Group) : Change()
    data class GetNearbyServiceLocationsError(val throwable: Throwable?) : Change()
}

data class State(
    val isLoading: Boolean,
    val serviceLocations: Group? = null,
    val isError: Boolean = false
) : BaseState
