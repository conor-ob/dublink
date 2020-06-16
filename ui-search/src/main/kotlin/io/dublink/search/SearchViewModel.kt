package io.dublink.search

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.dublink.domain.service.LocationProvider
import io.dublink.domain.service.PermissionChecker
import io.dublink.domain.service.PreferenceStore
import io.dublink.domain.service.RxScheduler
import io.dublink.domain.util.AppConstants
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.apache.lucene.queryparser.classic.ParseException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import timber.log.Timber

class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val permissionChecker: PermissionChecker,
    private val locationProvider: LocationProvider,
    private val preferenceStore: PreferenceStore,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State()

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.Loading -> state.copy(
                loading = true,
                scrollToTop = false,
                throwable = null
            )
            is Change.Error -> {
                Timber.w(change.throwable)
                if (change.throwable is ParseException) {
                    state.copy(
                        searchResults = SearchResultsResponse.NoResults(change.query, emptyList()),
                        scrollToTop = false,
                        loading = false,
                        throwable = null
                    )
                } else {
                    state.copy(
                        loading = false,
                        scrollToTop = false,
                        throwable = change.throwable
                    )
                }
            }
            is Change.NearbyLocations -> state.copy(
                nearbyLocations = change.nearbyLocations,
                scrollToTop = false,
                loading = false,
                throwable = null
            )
            is Change.SearchResults -> state.copy(
                searchResults = change.searchResults,
                scrollToTop = true,
                loading = false,
                throwable = null
            )
            is Change.RecentSearches -> state.copy(
                recentSearches = change.recentSearches,
                loading = false,
                scrollToTop = false,
                throwable = null
            )
            is Change.AddRecentSearch -> state.copy(
                loading = false,
                scrollToTop = false,
                throwable = null
            )
            is Change.ClearRecentSearches -> state.copy(
                loading = false,
                scrollToTop = false,
                throwable = null
            )
        }
    }

    init {
        bindActions()
    }

    private val locationSubscription = CompositeDisposable()

    fun onResume() {
        if (preferenceStore.isShowNearbyPlacesEnabled() && permissionChecker.isLocationPermissionGranted()) {
            locationSubscription.add(
                locationProvider.subscribeToLocationUpdates().subscribe()
            )
        }
    }

    fun onPause() {
        locationSubscription.clear()
    }

    private fun bindActions() {
        val searchResultsChange = actions.ofType(Action.Search::class.java)
            .debounce(AppConstants.searchQueryInputThrottling.toMillis(), TimeUnit.MILLISECONDS)
            .distinctUntilChanged { previousAction, currentAction ->
                if (previousAction.query == currentAction.query && currentAction.force) {
                    return@distinctUntilChanged false
                } else {
                    return@distinctUntilChanged previousAction.query == currentAction.query
                }
            }
            .switchMap { action ->
                searchUseCase.search(action.query)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.SearchResults(it) }
                    .throttleLatest(250L, TimeUnit.MILLISECONDS)
                    .onErrorReturn { Change.Error(action.query, it) }
                    .startWith(Change.Loading)
            }

        val getNearbyLocationsChange = actions.ofType(Action.GetNearbyLocations::class.java)
            .switchMap { _ ->
                searchUseCase.getNearbyServiceLocations()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.NearbyLocations(it) }
//                    .throttleLatest(500L, TimeUnit.MILLISECONDS)
                    .onErrorReturn { Change.Error("", it) }
            }

        val getRecentSearchesChange = actions.ofType(Action.GetRecentSearches::class.java)
            .switchMap { _ ->
                searchUseCase.getRecentSearches()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.RecentSearches(it) }
                    .onErrorReturn { Change.Error("", it) }
            }

        val addRecentSearchChange = actions.ofType(Action.AddRecentSearch::class.java)
            .switchMap { action ->
                searchUseCase.addRecentSearch(action.service, action.locationId)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.AddRecentSearch }
                    .onErrorReturn { Change.Error("", it) }
            }

        val clearRecentSearchesChange = actions.ofType(Action.ClearRecentSearches::class.java)
            .switchMap { _ ->
                searchUseCase.clearRecentSearches()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.ClearRecentSearches }
                    .onErrorReturn { Change.Error("", it) }
            }

        val allChanges = Observable.merge(
            listOf(
                searchResultsChange,
                getNearbyLocationsChange,
                getRecentSearchesChange,
                addRecentSearchChange,
                clearRecentSearchesChange
            )
        )

        disposables += allChanges
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }
}
