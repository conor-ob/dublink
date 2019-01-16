package ie.dublinmapper.view.nearby.livedata

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import ie.dublinmapper.domain.usecase.LiveDataUseCase
import ie.dublinmapper.model.LiveDataUi
import ie.dublinmapper.model.ServiceLocationUi
import ie.dublinmapper.util.Thread
import ie.dublinmapper.view.livedata.LiveDataMapper
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class NearbyLiveDataPresenterImpl @Inject constructor(
    private val useCase: LiveDataUseCase,
    private val thread: Thread
) : MvpBasePresenter<NearbyLiveDataView>(), NearbyLiveDataPresenter {

    private var viewModel: NearbyLiveDataViewModel = NearbyLiveDataViewModel()
    private var subscriptions: CompositeDisposable? = null

    override fun onViewAttached() {

    }

    override fun onViewDetached() {
        subscriptions?.clear()
        subscriptions?.dispose()
        subscriptions = null
    }

    override fun onLiveDataRequested(serviceLocation: ServiceLocationUi?) {
        viewModel = viewModel.copy(serviceLocation = serviceLocation)
        if (serviceLocation != null) {
            subscriptions().add(useCase.getCondensedLiveData(serviceLocation.serviceLocation)
                .subscribeOn(thread.io)
                .observeOn(thread.ui)
                .map { LiveDataMapper.map(it) }
                .doOnSubscribe {
//                    renderView()
                }
                .doOnNext {
                    viewModel = viewModel.copy(liveData = it, isLoading = false)
                    renderView()
                }
                .doOnError { Timber.e(it) }
                .subscribe()
            )
        } else {
            viewModel = viewModel.copy(liveData = emptyList())
        }
    }

    private fun renderView() {
        ifViewAttached { view -> view.render(viewModel) }
    }

    private fun subscriptions(): CompositeDisposable {
        if (subscriptions == null || subscriptions!!.isDisposed) {
            subscriptions = CompositeDisposable()
        }
        return subscriptions!!
    }

}

data class NearbyLiveDataViewModel(
    val serviceLocation: ServiceLocationUi? = null,
    val liveData: List<LiveDataUi> = emptyList(),
    val isLoading: Boolean = true
)
