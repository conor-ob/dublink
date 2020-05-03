package ie.dublinmapper.favourites

import com.ww.roxie.Reducer
import ie.dublinmapper.LifecycleAwareViewModel
import ie.dublinmapper.domain.internet.InternetStatusChangeListener
import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.service.PreferenceStore
import ie.dublinmapper.domain.service.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import timber.log.Timber

class FavouritesViewModel @Inject constructor(
    private val useCase: FavouritesUseCase,
    private val internetStatusChangeListener: InternetStatusChangeListener,
    private val preferenceStore: PreferenceStore,
    private val scheduler: RxScheduler
) : LifecycleAwareViewModel<Action, State>() {

    override val initialState = State(
        isLoading = true,
        favourites = null,
        favouritesWithLiveData = null,
        internetStatusChange = null
    )

    private val reducer: Reducer<State, NewState> = { state, newState ->
        when (newState) {
            is NewState.Favourites -> State(
                isLoading = state.isLoading,
                favourites = newState.favourites,
                favouritesWithLiveData = state.favouritesWithLiveData,
                internetStatusChange = null
            )
            is NewState.FavouriteServiceLocations -> State(
                isLoading = state.isLoading,
                favourites = mergeFavourites(state.favourites, newState.serviceLocations),
                favouritesWithLiveData = state.favouritesWithLiveData,
                internetStatusChange = null
            )
            is NewState.FavouritesWithLiveData -> State(
                isLoading = false,
                favourites = state.favourites,
                favouritesWithLiveData = mergeLiveData(state.favouritesWithLiveData, newState.favouritesWithLiveData),
                internetStatusChange = null
            )
            is NewState.InternetStatusChange -> State(
                isLoading = false,
                favourites = state.favourites,
                favouritesWithLiveData = state.favouritesWithLiveData,
                internetStatusChange = newState.internetStatusChange
            )
            is NewState.ClearLiveData -> State(
                isLoading = true,
                favourites = state.favourites,
                favouritesWithLiveData = null,
                internetStatusChange = null
            )
        }
    }

    private fun mergeFavourites(
        previousState: List<DubLinkServiceLocation>?,
        newState: List<DubLinkServiceLocation>
    ): List<DubLinkServiceLocation> {
        return if (previousState == null) {
            newState
        } else {
            val map = previousState.associateBy { it }.toMutableMap()
            for (entry in map) {
                val match = newState.find { it.service == entry.key.service && it.id == entry.key.id }
                if (match != null) {
                    map[entry.key] = match
                }
            }
            map.values.toList() // TODO sort
        }
    }

    /**
     * Due to the reactive way locations are observed, some responses may be "incomplete" and not contain locations from
     * all services if that particular service was slow due to a network request for example. If this happens then the
     * previous state is used for that missing location.
     */
    private fun mergeLiveData(
        previousState: List<LiveDataPresentationResponse>?,
        newState: List<LiveDataPresentationResponse>
    ): List<LiveDataPresentationResponse> =
        if (previousState == null) {
            newState
        } else {
            val mutableNewState = newState.toMutableList()
            for (liveData in previousState) {
                val match = newState.find { it.serviceLocation == liveData.serviceLocation }
                if (match == null) {
                    mutableNewState.add(liveData)
                }
            }
            mutableNewState
        }

    init {
        bindActions()
    }

    private fun bindActions() {
        val getFavouritesAction = actions.ofType(Action.GetFavourites::class.java)
            .switchMap {
                useCase.getFavourites()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<NewState> { NewState.Favourites(it) }
//                    .throttleLatest(250L, TimeUnit.MILLISECONDS)
            }

//        val getFavouriteServiceLocationsAction = actions.ofType(Action.GetFavouriteServiceLocations::class.java)
//            .switchMap {
//                useCase.getFavouriteServiceLocations()
//                    .subscribeOn(scheduler.io)
//                    .observeOn(scheduler.ui)
//                    .map<NewState> { NewState.FavouriteServiceLocations(it) }
//                    .throttleLatest(250L, TimeUnit.MILLISECONDS)
//            }

        val getLiveDataAction = actions.ofType(Action.GetLiveData::class.java)
            .switchMap {
                Observable.interval(0L, preferenceStore.getLiveDataRefreshInterval(), TimeUnit.SECONDS)
                    .filter { isActive() }
                    .flatMap { useCase.getLiveData(refresh = false) }
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<NewState> { NewState.FavouritesWithLiveData(it) }
                    .throttleLatest(250L, TimeUnit.MILLISECONDS)
            }

        val refreshLiveDataAction = actions.ofType(Action.RefreshLiveData::class.java)
            .switchMap {
                useCase.getLiveData(refresh = true)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<NewState> { NewState.FavouritesWithLiveData(it) }
                    .startWith(NewState.ClearLiveData)
                    .throttleLatest(250L, TimeUnit.MILLISECONDS)
            }

        val getInternetStatusChangeAction = actions.ofType(Action.SubscribeToInternetStatusChanges::class.java)
            .switchMap {
                internetStatusChangeListener.eventStream()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<NewState> { NewState.InternetStatusChange(it) }
            }

        val allActions = Observable.merge(
            listOf(
                getFavouritesAction,
//                getFavouriteServiceLocationsAction,
                getLiveDataAction,
                refreshLiveDataAction,
                getInternetStatusChangeAction
            )
        )

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }
}
