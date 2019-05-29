package ie.dublinmapper.view.nearby

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import ie.dublinmapper.domain.usecase.LiveDataUseCase
import ie.dublinmapper.domain.usecase.NearbyUseCase
import ie.dublinmapper.model.LiveDataUi
import ie.dublinmapper.model.ServiceLocationUi
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.LocationUtils
import ie.dublinmapper.util.RxScheduler
import ie.dublinmapper.view.livedata.LiveDataMapper
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NearbyPresenterImpl @Inject constructor(
    private val nearbyUseCase: NearbyUseCase,
    private val liveDataUseCase: LiveDataUseCase,
    private val scheduler: RxScheduler
) : MvpBasePresenter<NearbyView>(), NearbyPresenter {

    private var viewModel = NearbyViewModel()
    private var subscriptions: CompositeDisposable? = null

    override fun onViewAttached() {
        renderView()
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
        if (viewModel.cameraPosition == coordinate) {
            return
        }
        viewModel = viewModel.copy(cameraPosition = coordinate)
        val closestServiceLocation = findClosestServiceLocation(coordinate, viewModel)
        if (closestServiceLocation == null) {
            viewModel = viewModel.copy(closestServiceLocation = null)
        } else if (viewModel.closestServiceLocation != closestServiceLocation) {
            viewModel = viewModel.copy(closestServiceLocation = closestServiceLocation)
            subscriptions().add(liveDataUseCase.getLiveData(closestServiceLocation.serviceLocation)
            .subscribeOn(scheduler.io)
            .observeOn(scheduler.ui)
            .map { LiveDataMapper.map(it) }
            .doOnNext {
                viewModel = viewModel.copy(liveData = it)
                renderView()
            }
            .doOnError { Timber.e(it) }
            .subscribe()
        )
        }
        subscriptions().add(nearbyUseCase.getNearbyServiceLocations(coordinate)
            .debounce(100L, TimeUnit.MILLISECONDS)
            .subscribeOn(scheduler.io)
            .observeOn(scheduler.ui)
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

    private fun findClosestServiceLocation(coordinate: Coordinate, viewModel: NearbyViewModel): ServiceLocationUi? {
        val nearest = TreeMap<Double, ServiceLocationUi>()
        for (serviceLocation in viewModel.serviceLocations) {
            nearest[LocationUtils.haversineDistance(serviceLocation.coordinate, coordinate)] = serviceLocation
        }
        if (nearest.isEmpty()) {
            return null
        }
        val closest = nearest.iterator().next()
        if (closest.key < 25.0) {
            return closest.value
        }
        return null
    }

    private fun renderView() {
        ifViewAttached { view -> view.render(viewModel) }
    }

    fun onFocusedOnServiceLocation(serviceLocation: ServiceLocationUi) {
//        subscriptions().add(useCase.getCondensedLiveData(serviceLocation.serviceLocation)
//            .subscribeOn(scheduler.io)
//            .observeOn(scheduler.ui)
//            .map { LiveDataMapper.map(it) }
//            .doOnSubscribe {
//                ifViewAttached { view ->
//                    view.showServiceLocationColour(serviceLocation.colourId)
//                    view.showServiceLocation(serviceLocation)
//                }
//            }
//            .doOnNext { ifViewAttached { view -> view.showLiveData(it) } }
//            .doOnError { Timber.e(it) }
//            .subscribe()
//        )
    }

    private fun subscriptions(): CompositeDisposable {
        if (subscriptions == null || subscriptions!!.isDisposed) {
            subscriptions = CompositeDisposable()
        }
        return subscriptions!!
    }

}

data class NearbyViewModel(
    val closestServiceLocation: ServiceLocationUi? = null,
    val serviceLocations: List<ServiceLocationUi> = emptyList(),
    val liveData: List<LiveDataUi> = emptyList(),
    val isLoading: Boolean = true,
    val isMapReady: Boolean = false,
    val cameraPosition: Coordinate = Coordinate(0.0, 0.0),
    val bottomSheetHeight: Int = 600
)
