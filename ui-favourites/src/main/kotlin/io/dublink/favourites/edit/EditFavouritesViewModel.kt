package io.dublink.favourites.edit

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.service.RxScheduler
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
        editing = null,
        toastMessage = null,
        errorResponses = emptyList(),
        lastRemoved = null
    )

    private val reducer: Reducer<State, Result> = { state, result ->
        when (result) {
            is Result.FavouritesReceived -> State(
                isFinished = false,
                original = if (state.original.isNullOrEmpty()) result.favourites else state.original,
                editing = if (state.editing.isNullOrEmpty()) result.favourites else state.editing,
                toastMessage = null,
                errorResponses = result.errorResponses,
                lastRemoved = null
            )
            is Result.FavouriteEdited -> State(
                isFinished = false,
                original = state.original,
                editing = merge(result.serviceLocation, state.editing),
                toastMessage = null,
                errorResponses = emptyList(),
                lastRemoved = null
            )
            is Result.FavouritesReordered -> State(
                isFinished = false,
                original = state.original,
                editing = result.serviceLocations,
                toastMessage = null,
                errorResponses = emptyList(),
                lastRemoved = null
            )
            is Result.FavouritesSaved -> State(
                isFinished = true,
                original = state.original,
                editing = state.editing,
                toastMessage = null,
                errorResponses = emptyList(),
                lastRemoved = null
            )
            is Result.Error -> State(
                isFinished = false,
                original = state.original,
                editing = state.editing,
                toastMessage = "Something went wrong, try refreshing",
                errorResponses = emptyList(),
                lastRemoved = null
            )
            is Result.FavouriteRemoved -> State(
                isFinished = false,
                original = state.original,
                editing = result.serviceLocations,
                toastMessage = null,
                errorResponses = emptyList(),
                lastRemoved = result.lastRemoved
            )
            is Result.UndoRemoveFavourite -> {
                val editing = state.editing?.toMutableList()
                val lastRemoved = state.lastRemoved
                if (editing != null && lastRemoved != null) {
                    editing.add(lastRemoved.index, lastRemoved.serviceLocation as DubLinkServiceLocation)
                }
                State(
                    isFinished = false,
                    original = state.original,
                    editing = editing,
                    toastMessage = null,
                    errorResponses = emptyList(),
                    lastRemoved = null
                )
            }
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
                            is FavouritesResponse.Data -> Result.FavouritesReceived(response.serviceLocations, response.errorResponses)
                            is FavouritesResponse.Error -> Result.Error(response.throwable)
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

        val favouriteRemovedActions = actions.ofType(Action.FavouriteRemoved::class.java)
            .switchMap { action ->
                Observable.just(action.serviceLocations)
                    .map<Result> { serviceLocations -> Result.FavouriteRemoved(serviceLocations, LastRemoved(action.index, action.serviceLocation)) }
            }

        val undoRemoveFavouriteActions = actions.ofType(Action.UndoRemoveFavourite::class.java)
            .switchMap {
                Observable.just(true)
                    .map<Result> { Result.UndoRemoveFavourite }
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
                favouriteRemovedActions,
                undoRemoveFavouriteActions,
                saveChangesActions
            )
        )

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }
}
