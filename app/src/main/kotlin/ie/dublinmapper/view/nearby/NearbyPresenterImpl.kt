package ie.dublinmapper.view.nearby

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import ie.dublinmapper.domain.usecase.NearbyUseCase
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Thread
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NearbyPresenterImpl @Inject constructor(
    private val useCase: NearbyUseCase,
    private val thread: Thread
) : MvpBasePresenter<NearbyView>(), NearbyPresenter {

    private var subscriptions: CompositeDisposable? = null

    override fun onViewAttached() {

    }

    override fun onViewDetached() {
        subscriptions?.clear()
        subscriptions?.dispose()
        subscriptions = null
    }

    override fun onCameraMoved(coordinate: Coordinate) {
        subscriptions().add(useCase.getNearbyServiceLocations(coordinate)
            .debounce(100L, TimeUnit.MILLISECONDS)
            .subscribeOn(thread.io)
            .observeOn(thread.ui)
            .doOnNext { ifViewAttached { view -> view.showServiceLocations(it) } }
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
