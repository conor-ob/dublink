package ie.dublinmapper.favourites

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import com.xwray.groupie.Group
import com.xwray.groupie.Section
import ie.dublinmapper.domain.usecase.FavouritesUseCase
import ie.dublinmapper.domain.service.RxScheduler
import ie.dublinmapper.domain.usecase.FavouritesResponse
import ie.dublinmapper.domain.usecase.LiveDataResponse
import ie.dublinmapper.domain.usecase.SearchResponse
import ie.dublinmapper.model.DividerItem
import io.reactivex.rxkotlin.plusAssign
import io.rtpi.api.LiveData
import io.rtpi.api.ServiceLocation
import ma.glasnost.orika.MapperFacade
import timber.log.Timber
import javax.inject.Inject

class FavouritesViewModel @Inject constructor(
    private val useCase: FavouritesUseCase,
    private val mapper: MapperFacade,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(isLoading = true)

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.Loading -> state.copy(
                isLoading = true,
                favourites = null,
                isError = false
            )
            is Change.GetFavourites -> state.copy(
                isLoading = false,
                favourites = change.favourites,
                isError = false
            )
            is Change.GetFavouritesError -> state.copy(
                isLoading = false,
                favourites = null,
                isError = true //TODO
            )
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val getFavouritesChange = actions.ofType(Action.GetFavourites::class.java)
            .switchMap { _ ->
                useCase.getFavourites()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Group> { mapper.map(it, Group::class.java) }
                    .map<Change> { Change.GetFavourites(it) }
                    .onErrorReturn {
                        Timber.e(it)
                        Change.GetFavouritesError(it)
                    }
                    .startWith(Change.Loading)
            }

        disposables += getFavouritesChange
            .scan(initialState, reducer)
//            .filter { !it.isLoading }
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }
}
