package ie.dublinmapper.livedata

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.xwray.groupie.Group
import ie.dublinmapper.util.Service

sealed class Action : BaseAction {

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
    data class GetLiveData(val liveData: Group) : Change()
    object FavouriteSaved : Change()
    object FavouriteRemoved : Change()
    data class GetLiveDataError(val throwable: Throwable?) : Change()
}

data class State(
    val isLoading: Boolean,
    val liveData: Group? = null,
    val isError: Boolean = false,
    val isFavourite: Boolean = false
) : BaseState
