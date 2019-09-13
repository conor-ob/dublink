package ie.dublinmapper.livedata

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import com.xwray.groupie.Group
import ie.dublinmapper.domain.usecase.LiveDataUseCase
import ie.dublinmapper.util.RxScheduler
import io.reactivex.rxkotlin.plusAssign
import ma.glasnost.orika.MapperFacade
import timber.log.Timber
import javax.inject.Inject

class LiveDataViewModel @Inject constructor(
    private val useCase: LiveDataUseCase,
    private val mapper: MapperFacade,
    private val scheduler: RxScheduler
) : BaseViewModel<Action, State>() {

    override val initialState = State(isLoading = true)

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.Loading -> state.copy(
                isLoading = true,
                liveData = null,
                isError = false
            )
            is Change.GetLiveData -> state.copy(
                isLoading = false,
                liveData = change.liveData,
                isError = false
            )
            is Change.GetLiveDataError -> state.copy(
                isLoading = false,
                liveData = null,
                isError = true //TODO
            )
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val getLiveDataChange = actions.ofType(Action.GetLiveData::class.java)
            .switchMap { action ->
                useCase.getCondensedLiveDataStream(
                    action.serviceLocationId,
                    action.serviceLocationName,
                    action.serviceLocationService
                )
                    .subscribeOn(scheduler.io)
                    .observeOn(scheduler.ui)
                    .map<Group> { mapper.map(it, Group::class.java) }
                    .map<Change> { Change.GetLiveData(it) }
                    .onErrorReturn { Change.GetLiveDataError(it) }
                    .startWith(Change.Loading)
            }

        disposables += getLiveDataChange
            .scan(initialState, reducer)
//            .filter { !it.isLoading }
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }

}
