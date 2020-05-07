package io.dublink.favourites

import com.ww.roxie.Reducer
import io.dublink.LifecycleAwareViewModel
import io.dublink.domain.internet.InternetStatusChangeListener
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.service.PreferenceStore
import io.dublink.domain.service.RxScheduler
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
                isLoading = false,
                favourites = newState.favourites,
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
        newState: List<LiveDataPresentationResponse>
    ): List<DubLinkServiceLocation> {
        return if (previousState == null) {
            newState.map { it.serviceLocation }
        } else {
            val map = previousState.associateBy { it }.toMutableMap()
            for (entry in map) {
                val match = newState.find { it.serviceLocation.service == entry.key.service && it.serviceLocation.id == entry.key.id }
                if (match != null) {
                    map[entry.key] = match.serviceLocation
                }
            }
            map.values.toList()
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
            }

        val getLiveDataAction = actions.ofType(Action.GetLiveData::class.java)
            .switchMap {
                Observable.interval(0L, preferenceStore.getLiveDataRefreshInterval(), TimeUnit.SECONDS)
                    .filter { isActive() }
                    .flatMap { useCase.getLiveData(refresh = false) }
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<NewState> { NewState.FavouritesWithLiveData(it) }
            }

        val refreshLiveDataAction = actions.ofType(Action.RefreshLiveData::class.java)
            .switchMap {
                useCase.getLiveData(refresh = true)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<NewState> { NewState.FavouritesWithLiveData(it) }
                    .startWith(NewState.ClearLiveData)
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
                getLiveDataAction,
                refreshLiveDataAction,
                getInternetStatusChangeAction
            )
        )

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .throttleLatest(500L, TimeUnit.MILLISECONDS)
            .subscribe(state::postValue, Timber::e)
    }
}
