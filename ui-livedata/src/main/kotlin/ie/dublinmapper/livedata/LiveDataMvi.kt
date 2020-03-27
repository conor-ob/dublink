package ie.dublinmapper.livedata

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.xwray.groupie.Group
import io.rtpi.api.LiveData
import io.rtpi.api.Service
import io.rtpi.api.ServiceLocation

sealed class Action : BaseAction {

    data class GetServiceLocation(
        val serviceLocationId: String,
        val serviceLocationService: Service
    ) : Action()

    data class GetLiveData(
        val serviceLocationId: String,
        val serviceLocationName: String,
        val serviceLocationService: Service
    ) : Action()

    data class SaveFavourite(
        val serviceLocationId: String,
        val serviceLocationName: String,
        val serviceLocationService: Service
    ) : Action()

    data class RemoveFavourite(
        val serviceLocationId: String,
        val serviceLocationService: Service
    ) : Action()
}

sealed class Change {
    object Loading : Change()
    data class GetServiceLocation(val serviceLocation: ServiceLocation) : Change()
//    data class GetLiveData(val liveData: List<LiveData>) : Change()
    data class GetLiveData(val liveData: Group) : Change()
    object FavouriteSaved : Change()
    object FavouriteRemoved : Change()
    data class GetLiveDataError(val throwable: Throwable?) : Change()
}

data class State(
    val serviceLocation: ServiceLocation? = null,
    val isLoading: Boolean = false,
    val liveData: Group? = null,
    val isError: Boolean = false,
    val isFavourite: Boolean = false
) : BaseState
