package ie.dublinmapper.view.livedata

import com.xwray.groupie.Group
import ie.dublinmapper.domain.usecase.FavouritesUseCase
import ie.dublinmapper.domain.usecase.LiveDataUseCase
import ie.dublinmapper.util.RxScheduler
import ie.dublinmapper.util.Service
import ie.dublinmapper.view.BasePresenter
import ma.glasnost.orika.MapperFacade
import timber.log.Timber
import javax.inject.Inject

class LiveDataPresenterImpl @Inject constructor(
    private val useCase: LiveDataUseCase,
    private val favouritesUseCase: FavouritesUseCase,
    private val mapper: MapperFacade,
    scheduler: RxScheduler
) : BasePresenter<LiveDataView>(scheduler), LiveDataPresenter {

    override fun start(serviceLocationId: String, serviceLocationName: String, service: Service) {
        subscriptions().add(useCase.getCondensedLiveDataStream(serviceLocationId, serviceLocationName, service)
            .compose(applySchedulers())
            .map { liveData -> mapper.map(liveData, Group::class.java) }
            .doOnNext { ifViewAttached { view -> view.showLiveData(it) } }
            .doOnError { Timber.e(it) }
            .subscribe( {Timber.d(it.toString())} , {Timber.e(it)} )
        )
    }

    override fun stop() {
        unsubscribe()
    }

    override fun onSaveFavouritePressed(serviceLocationId: String, serviceLocationName: String, service: Service) {
        subscriptions().add(favouritesUseCase.saveFavourite(serviceLocationId, serviceLocationName, service)
            .compose(applyCompletableSchedulers())
            .doOnComplete { ifViewAttached { view -> view.showFavouriteSaved() } }
            .subscribe()
        )
    }

    override fun onRemoveFavouritePressed(serviceLocationId: String, service: Service) {
        subscriptions().add(favouritesUseCase.removeFavourite(serviceLocationId, service)
            .compose(applyCompletableSchedulers())
            .doOnComplete { ifViewAttached { view -> view.showFavouriteRemoved() } }
            .subscribe()
        )
    }

}
