package io.dublink.favourites

import com.ww.roxie.Reducer
import io.dublink.LifecycleAwareViewModel
import io.dublink.domain.service.LocationProvider
import io.dublink.domain.service.PermissionChecker
import io.dublink.domain.service.PreferenceStore
import io.dublink.domain.service.RxScheduler
import io.dublink.domain.util.AppConstants
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.rtpi.api.PredictionLiveData
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import timber.log.Timber

class FavouritesViewModel @Inject constructor(
    private val useCase: FavouritesUseCase,
    private val permissionChecker: PermissionChecker,
    private val locationProvider: LocationProvider,
    private val preferenceStore: PreferenceStore,
    private val scheduler: RxScheduler
) : LifecycleAwareViewModel<Action, State>() {

    override val initialState = State(
        isLoading = true,
        favourites = null,
        favouritesWithLiveData = null
    )

    private val reducer: Reducer<State, NewState> = { state, newState ->
        when (newState) {
            is NewState.Favourites -> State(
                isLoading = false,
                favourites = newState.favourites,
                favouritesWithLiveData = state.favouritesWithLiveData
            )
            is NewState.FavouritesWithLiveData -> State(
                isLoading = false,
                favourites = state.favourites,
                favouritesWithLiveData = mergeLiveData(state.favouritesWithLiveData, newState.favouritesWithLiveData)
            )
            is NewState.ClearLiveData -> State(
                isLoading = true,
                favourites = state.favourites,
                favouritesWithLiveData = null
            )
            is NewState.RefreshIfStale -> State(
                isLoading = state.isLoading,
                favourites = state.favourites,
                favouritesWithLiveData = checkForStaleData(state.favouritesWithLiveData)
            )
        }
    }

    private fun checkForStaleData(
        favouritesWithLiveData: List<LiveDataPresentationResponse>?
    ): List<LiveDataPresentationResponse>? {
        return if (favouritesWithLiveData == null) {
            favouritesWithLiveData
        } else {
            val isStale = favouritesWithLiveData
                .filterIsInstance<LiveDataPresentationResponse.Data>()
                .flatMap { it.liveData.flatten() }
                .filterIsInstance<PredictionLiveData>()
                .any { Duration.between(it.prediction.currentDateTime, ZonedDateTime.now()) > AppConstants.liveDataTimeToLive }
            if (isStale) {
                null
            } else {
                favouritesWithLiveData
            }
        }
    }

    /**
     * A bit of a hack to not collapse every item and show the loading message every time data is refreshed - it should
     * only be shown when the user triggers a refresh or the data is stale when the app returns from the background.
     */
    private fun mergeLiveData(
        previousState: List<LiveDataPresentationResponse>?,
        newState: List<LiveDataPresentationResponse>
    ): List<LiveDataPresentationResponse> =
        if (previousState == null) {
            newState
        } else {
            newState.map { state ->
                if (state is LiveDataPresentationResponse.Loading) {
                    // If the new state is loading just use the previous state so we don't show too many loading events
                    previousState.find { it.serviceLocation.service == state.serviceLocation.service &&
                        it.serviceLocation.id == state.serviceLocation.id } ?: state
                } else {
                    state
                }
            }
        }

    init {
        bindActions()
    }

    private val locationSubscription = CompositeDisposable()

    override fun onResume() {
        super.onResume()
        if (preferenceStore.isFavouritesSortByLocation() && permissionChecker.isLocationPermissionGranted()) {
            locationSubscription.add(
                locationProvider.subscribeToLocationUpdates().subscribe()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        locationSubscription.clear()
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
                Observable.interval(Duration.ZERO.seconds, preferenceStore.getLiveDataRefreshInterval(), TimeUnit.SECONDS)
                    .filter { isActive() }
                    .flatMap { useCase.getLiveData(refresh = false) }
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<NewState> { NewState.FavouritesWithLiveData(it) }
                    .startWith(NewState.RefreshIfStale)
            }

        val refreshLiveDataAction = actions.ofType(Action.RefreshLiveData::class.java)
            .switchMap {
                useCase.getLiveData(refresh = true)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<NewState> { NewState.FavouritesWithLiveData(it) }
                    .startWith(NewState.ClearLiveData)
            }

        val allActions = Observable.merge(
            listOf(
                getFavouritesAction,
                getLiveDataAction,
                refreshLiveDataAction
            )
        )

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .throttleLatest(AppConstants.favouritesUiEventThrottling.toMillis(), TimeUnit.MILLISECONDS)
            .subscribe(state::postValue, Timber::e)
    }
}
