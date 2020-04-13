package ie.dublinmapper.nearby

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import com.xwray.groupie.Group
import ie.dublinmapper.domain.service.RxScheduler
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject
import ma.glasnost.orika.MapperFacade
import timber.log.Timber

class NearbyViewModel @Inject constructor(
    private val useCase: NearbyUseCase,
    private val mapper: MapperFacade,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(isLoading = true)

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.Loading -> state.copy(
                isLoading = true,
                isError = false
            )
            is Change.GetNearbyServiceLocations -> state.copy(
                isLoading = false,
                serviceLocations = change.serviceLocations,
                isError = false
            )
            is Change.GetNearbyServiceLocationsError -> state.copy(
                isLoading = false,
                isError = true
            )
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val getLocationChange = actions.ofType(Action.GetNearbyServiceLocations::class.java)
            .switchMap { action ->
                useCase.getNearbyServiceLocations()
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Group> { mapper.map(it, Group::class.java) }
                    .map<Change> { Change.GetNearbyServiceLocations(it) }
                    .onErrorReturn { Change.GetNearbyServiceLocationsError(it) }
                    .startWith(Change.Loading)
            }

        disposables += getLocationChange
            .scan(initialState, reducer)
//            .filter { !it.isLoading }
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }
}
