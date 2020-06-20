package io.dublink.nearby

import com.ww.roxie.Reducer
import io.dublink.LifecycleAwareViewModel
import io.dublink.domain.service.RxScheduler
import io.dublink.domain.util.AppConstants
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NearbyViewModel @Inject constructor(
    private val useCase: NearbyUseCase,
    private val scheduler: RxScheduler
) : LifecycleAwareViewModel<Action, State>() {

    override val initialState = State(
        nearbyServiceLocations = emptyList(),
        focusedServiceLocation = null,
        focusedServiceLocationLiveData = null
    )

    private val reducer: Reducer<State, NewState> = { state, newState ->
        when (newState) {
            is NewState.NearbyServiceLocations -> State(
                nearbyServiceLocations = newState.serviceLocations,
                focusedServiceLocation = state.focusedServiceLocation,
                focusedServiceLocationLiveData = state.focusedServiceLocationLiveData
            )
            is NewState.GetLiveData -> State(
                nearbyServiceLocations = state.nearbyServiceLocations,
                focusedServiceLocation = newState.serviceLocation,
                focusedServiceLocationLiveData = newState.liveData
            )
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val onMapMoveFinishedActions = actions.ofType(Action.OnMapMoveFinished::class.java)
            .switchMap { action ->
                useCase.getNearbyServiceLocations(action.coordinate)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .filter { it.isNotEmpty() } // TODO check why this is needed
                    .map<NewState> { NewState.NearbyServiceLocations(it) }
            }

        val getLiveDataActions = actions.ofType(Action.GetLiveData::class.java)
            .switchMap { action ->
                useCase.getLiveData(action.serviceLocation)
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<NewState> { NewState.GetLiveData(action.serviceLocation, it) }
                    .startWith(NewState.GetLiveData(action.serviceLocation, null))
            }

        val allActions = Observable.merge(
            listOf(
                onMapMoveFinishedActions,
                getLiveDataActions
            )
        )

        disposables += allActions
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .throttleLatest(500L, TimeUnit.MILLISECONDS)
            .subscribe(state::postValue, Timber::e)
    }
}
