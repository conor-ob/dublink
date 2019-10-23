package ie.dublinmapper.nearby

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import ie.dublinmapper.domain.usecase.NearbyUseCase
import ie.dublinmapper.util.RxScheduler
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class NearbyViewModel @Inject constructor(
    private val useCase: NearbyUseCase,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(isLoading = true)

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.Loading -> state.copy(
                isLoading = true,
                isError = false
            )
            is Change.GetLocation -> state.copy(
                isLoading = false,
                location = change.location,
                isError = false
            )
            is Change.GetLocationError -> state.copy(
                isLoading = false,
                isError = true
            )
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val getLocationChange = actions.ofType(Action.GetLocation::class.java)
            .switchMap { action ->
                useCase.getLocationUpdates()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Change> { Change.GetLocation(it) }
                    .onErrorReturn { Change.GetLocationError(it) }
                    .startWith(Change.Loading)
            }

        disposables += getLocationChange
            .scan(initialState, reducer)
//            .filter { !it.isLoading }
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }

}
