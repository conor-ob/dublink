package ie.dublinmapper.view.nearby.map

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import ie.dublinmapper.domain.usecase.NearbyUseCase
import ie.dublinmapper.model.ServiceLocationUi
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.LocationUtils
import ie.dublinmapper.util.Thread
import ie.dublinmapper.view.nearby.NearbyMapper
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NearbyMapPresenterImpl @Inject constructor(
    private val nearbyUseCase: NearbyUseCase,
    private val thread: Thread
) : MvpBasePresenter<NearbyMapView>(), NearbyMapPresenter {

    private var viewModel = NearbyMapViewModel()
    private var subscriptions: CompositeDisposable? = null

    override fun onViewAttached() {
    }

    override fun onViewDetached() {
        subscriptions?.clear()
        subscriptions?.dispose()
        subscriptions = null
    }

    override fun onViewDestroyed() {
        viewModel = viewModel.copy(isMapReady = false)
        renderView()
    }

    override fun onMapReady() {
        if (viewModel.isMapReady) {
            return
        }
        viewModel = viewModel.copy(isMapReady = true)
        renderView()
    }

    override fun onCameraMoved(coordinate: Coordinate) {
        viewModel = viewModel.copy(serviceLocation = findClosestServiceLocation(coordinate, viewModel.serviceLocations))
        subscriptions().add(nearbyUseCase.getNearbyServiceLocations(coordinate)
            .debounce(100L, TimeUnit.MILLISECONDS)
            .subscribeOn(thread.io)
            .observeOn(thread.ui)
            .doOnNext { response ->
                viewModel = viewModel.copy(
                    serviceLocations = NearbyMapper.map(response.serviceLocations),
                    isLoading = !response.isComplete
                )
                renderView()
            }
            .doOnError { Timber.e(it) }
            .subscribe()
        )
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

    private fun findClosestServiceLocation(
        coordinate: Coordinate,
        serviceLocations: List<ServiceLocationUi>
    ): ServiceLocationUi? {
        val nearest = TreeMap<Double, ServiceLocationUi>()
        for (serviceLocation in serviceLocations) {
            nearest[LocationUtils.haversineDistance(serviceLocation.coordinate, coordinate)] = serviceLocation
        }
        if (nearest.isEmpty()) {
            return null
        }
        val closest = nearest.iterator().next()
        if (closest.key < 50.0) {
            return closest.value
        }
        return null
    }

}

data class NearbyMapViewModel(
    val serviceLocation: ServiceLocationUi? = null,
    val serviceLocations: List<ServiceLocationUi> = emptyList(),
    val isLoading: Boolean = true,
    val isMapReady: Boolean = false
)
