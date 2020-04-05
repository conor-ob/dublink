package ie.dublinmapper.search

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import com.xwray.groupie.Group
import ie.dublinmapper.domain.usecase.SearchUseCase
import ie.dublinmapper.domain.service.RxScheduler
import ie.dublinmapper.mapping.SearchResponseMapper
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import ma.glasnost.orika.MapperFacade
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val mapper: MapperFacade,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(isLoading = false)

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.Loading -> state.copy(
                isLoading = true,
                isError = false
            )
            is Change.NearbyLocations -> state.copy(
                nearbyLocations = change.nearbyLocations,
                isLoading = false,
                isError = false
            )
            is Change.SearchResults -> state.copy(
                isLoading = false,
                searchResults = change.searchResults,
                isError = false
            )
            is Change.SearchResultsError -> state.copy(
                isLoading = false,
                searchResults = null,
                isError = true //TODO
            )
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val searchResultsChange = actions.ofType(Action.Search::class.java)
            .debounce(400L, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .switchMap { action ->
                Timber.d("Searching for ${action.query}")
                searchUseCase.search(action.query)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Group> { mapper.map(it, Group::class.java) }
                    .map<Change> { Change.SearchResults(it) }
                    .onErrorReturn {
                        Timber.e(it)
                        Change.SearchResultsError(it)
                    }
                    .startWith(Change.Loading)
            }

        val getNearbyLocationsChange = actions.ofType(Action.GetNearbyLocations::class.java)
            .switchMap { _ ->
                searchUseCase.getNearbyServiceLocations()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Group> { SearchResponseMapper.mapNearbyLocations(it) }
                    .map<Change> { Change.NearbyLocations(it) }
            }

        val allChanges = Observable.merge(searchResultsChange, getNearbyLocationsChange)

        disposables += allChanges
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }

}
