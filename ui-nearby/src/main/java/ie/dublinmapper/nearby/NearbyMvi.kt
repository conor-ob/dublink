package ie.dublinmapper.nearby

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import io.rtpi.api.Coordinate

sealed class Action : BaseAction {
    object GetLocation : Action()
}

sealed class Change {
    object Loading : Change()
    data class GetLocation(val location: Coordinate) : Change()
    data class GetLocationError(val throwable: Throwable?) : Change()
}

data class State(
    val isLoading: Boolean,
    val location: Coordinate? = null,
    val isError: Boolean = false
) : BaseState
