package ie.dublinmapper.search

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import com.xwray.groupie.Group
import ie.dublinmapper.domain.usecase.SearchUseCase
import ie.dublinmapper.core.RxScheduler
import io.reactivex.rxkotlin.plusAssign
import ma.glasnost.orika.MapperFacade
import timber.log.Timber
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val useCase: SearchUseCase,
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
            .switchMap { action ->
                useCase.search(action.query)
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

        disposables += searchResultsChange
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }

}
