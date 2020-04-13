package ie.dublinmapper.search

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import ie.dublinmapper.domain.service.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State()

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.NearbyLocations -> state.copy(
                nearbyLocations = change.nearbyLocations
            )
            is Change.SearchResults -> state.copy(
                searchResults = change.searchResults
            )
            is Change.RecentSearches -> state.copy(
                recentSearches = change.recentSearches
            )
            is Change.AddRecentSearch -> state
        }
    }

    fun bindActions() {
        val searchResultsChange = actions.ofType(Action.Search::class.java)
            .debounce(400L, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .switchMap { action ->
                searchUseCase.search(action.query)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.SearchResults(it) }
            }

        val getNearbyLocationsChange = actions.ofType(Action.GetNearbyLocations::class.java)
            .switchMap { _ ->
                searchUseCase.getNearbyServiceLocations()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.NearbyLocations(it) }
            }

        val getRecentSearchesChange = actions.ofType(Action.GetRecentSearches::class.java)
            .switchMap { _ ->
                searchUseCase.getRecentSearches()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.RecentSearches(it) }
            }

        val addRecentSearchChange = actions.ofType(Action.AddRecentSearch::class.java)
            .switchMap { action ->
                searchUseCase.addRecentSearch(action.service, action.locationId)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.AddRecentSearch }
            }

        val allChanges = Observable.merge(searchResultsChange, getNearbyLocationsChange, getRecentSearchesChange, addRecentSearchChange)

        disposables += allChanges
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }

    fun unbindActions() {
        disposables.clear()
    }
}
