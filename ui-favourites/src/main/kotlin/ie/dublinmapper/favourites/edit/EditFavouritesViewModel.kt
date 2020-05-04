package ie.dublinmapper.favourites.edit

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import ie.dublinmapper.domain.model.DubLinkServiceLocation
import ie.dublinmapper.domain.service.RxScheduler
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject
import timber.log.Timber

class EditFavouritesViewModel @Inject constructor(
    private val editFavouritesUseCase: EditFavouritesUseCase,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(
        isFinished = false,
        original = null,
        editing = null
    )

    private val reducer: Reducer<State, Result> = { state, result ->
        when (result) {
            is Result.FavouritesReceived -> State(
                isFinished = false,
                original = state.original ?: result.favourites,
                editing = state.editing ?: result.favourites
            )
            is Result.FavouriteEdited -> State(
                isFinished = false,
                original = state.original,
                editing = merge(result.serviceLocation, state.editing)
            )
            is Result.FavouritesReordered -> State(
                isFinished = false,
                original = state.original,
                editing = result.serviceLocations
            )
            is Result.FavouritesSaved -> State(
                isFinished = true,
                original = state.original,
                editing = state.editing
            )
        }
    }

    private fun merge(
        previous: List<DubLinkServiceLocation>?,
        next: List<DubLinkServiceLocation>
    ): List<DubLinkServiceLocation> {
        return if (previous == null) {
            next
        } else {
            val mutablePreviousState = previous.associateBy { it }.toMutableMap()
            for (location in previous) {
                val match = next.find { it.id == location.id }
                if (match != null) {
                    mutablePreviousState[location] = match
                }
            }
            mutablePreviousState.values.toList().sortedBy { it.favouriteSortIndex }
        }
    }

//    private fun merge(
//        previous: List<DubLinkServiceLocation>?,
//        next: List<DubLinkServiceLocation>
//    ): List<DubLinkServiceLocation> {
//        return if (previous == null) {
//            next
//        } else {
//            val mutablePreviousState = previous.toMutableList()
//            for (location in next) {
//                val match = previous.find { it.id == location.id }
//                if (match == null) {
//                    mutablePreviousState.add(location)
//                }
//            }
//            mutablePreviousState.sortedBy { it.favouriteSortIndex }
//        }
//    }

    private fun merge(serviceLocation: DubLinkServiceLocation, editing: List<DubLinkServiceLocation>?): List<DubLinkServiceLocation>? {
        return if (editing.isNullOrEmpty()) {
            listOf(serviceLocation) // TODO shouldn't happen
        } else {
            val match = editing.find { it.service == serviceLocation.service && it.id == serviceLocation.id }
            val copy = editing.toMutableList()
            val index = copy.indexOf(match)
            copy.removeAt(index)
            copy.add(index, serviceLocation)
            copy
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val getFavouritesActions = actions.ofType(Action.GetFavourites::class.java)
            .switchMap {
                editFavouritesUseCase.getFavourites()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Result> { response ->
                        when (response) {
                            is FavouritesResponse.Data -> Result.FavouritesReceived(response.serviceLocations)
                            is FavouritesResponse.Error -> Result.FavouritesReceived(emptyList()) // TODO
                        }
                    }
            }

        val editServiceLocationAction = actions.ofType(Action.EditFavourite::class.java)
            .switchMap { action ->
                Observable.just(action.serviceLocation)
                    .map<Result> { serviceLocation -> Result.FavouriteEdited(serviceLocation) }
            }

        val favouritesReorderedActions = actions.ofType(Action.FavouritesReordered::class.java)
            .switchMap { action ->
                Observable.just(action.serviceLocations)
                    .map<Result> { serviceLocations -> Result.FavouritesReordered(serviceLocations) }
            }

        val saveChangesActions = actions.ofType(Action.SaveChanges::class.java)
            .switchMap { action ->
                editFavouritesUseCase.saveChanges(action.serviceLocations)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Result> { Result.FavouritesSaved }
            }

        val allActions = Observable.merge(
            listOf(
                getFavouritesActions,
                editServiceLocationAction,
                favouritesReorderedActions,
                saveChangesActions
            )
        )

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }
}
