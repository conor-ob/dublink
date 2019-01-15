package ie.dublinmapper.view.nearby.livedata

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import ie.dublinmapper.domain.usecase.LiveDataUseCase
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

    private var subscriptions: CompositeDisposable? = null

    override fun onViewAttached() {

    }

    override fun onViewDetached() {
        subscriptions?.clear()
        subscriptions?.dispose()
        subscriptions = null
    }

    override fun onFocusedOnServiceLocation(serviceLocation: ServiceLocationUi) {
        subscriptions().add(useCase.getCondensedLiveData(serviceLocation.serviceLocation)
            .subscribeOn(thread.io)
            .observeOn(thread.ui)
            .map { LiveDataMapper.map(it) }
            .doOnSubscribe {
                ifViewAttached { view ->
                    view.showServiceLocationColour(serviceLocation.colourId)
                    view.showServiceLocation(serviceLocation)
                }
            }
            .doOnNext { ifViewAttached { view -> view.showLiveData(it) } }
            .doOnError { Timber.e(it) }
            .subscribe()
        )
    }

    private fun subscriptions(): CompositeDisposable {
        if (subscriptions == null || subscriptions!!.isDisposed) {
            subscriptions = CompositeDisposable()
        }
        return subscriptions!!
    }

}
