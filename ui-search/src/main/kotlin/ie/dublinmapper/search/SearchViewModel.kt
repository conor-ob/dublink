package ie.dublinmapper.search

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import ie.dublinmapper.domain.service.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import timber.log.Timber

class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
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
            is Change.Error -> state.copy(
                loading = false,
                scrollToTop = false,
                throwable = change.throwable
            )
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

    private fun bindActions() {
        val searchResultsChange = actions.ofType(Action.Search::class.java)
            .debounce(500L, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .switchMap { action ->
                searchUseCase.search(action.query)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.SearchResults(it) }
                    .onErrorReturn { Change.Error(it) }
                    .startWith(Change.Loading)
            }

        val getNearbyLocationsChange = actions.ofType(Action.GetNearbyLocations::class.java)
            .switchMap { _ ->
                searchUseCase.getNearbyServiceLocations()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.NearbyLocations(it) }
                    .onErrorReturn { Change.Error(it) }
            }

        val getRecentSearchesChange = actions.ofType(Action.GetRecentSearches::class.java)
            .switchMap { _ ->
                searchUseCase.getRecentSearches()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.RecentSearches(it) }
                    .onErrorReturn { Change.Error(it) }
            }

        val addRecentSearchChange = actions.ofType(Action.AddRecentSearch::class.java)
            .switchMap { action ->
                searchUseCase.addRecentSearch(action.service, action.locationId)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.AddRecentSearch }
                    .onErrorReturn { Change.Error(it) }
            }

        val clearRecentSearchesChange = actions.ofType(Action.ClearRecentSearches::class.java)
            .switchMap { _ ->
                searchUseCase.clearRecentSearches()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.ClearRecentSearches }
                    .onErrorReturn { Change.Error(it) }
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
