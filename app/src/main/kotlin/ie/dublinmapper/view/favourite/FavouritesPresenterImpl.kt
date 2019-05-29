package ie.dublinmapper.view.favourite

import ie.dublinmapper.domain.usecase.FavouritesUseCase
import ie.dublinmapper.util.RxScheduler
import ie.dublinmapper.util.ServiceLocationUiMapper
import ie.dublinmapper.view.BasePresenter
import timber.log.Timber
import javax.inject.Inject

class FavouritesPresenterImpl @Inject constructor(
    private val useCase: FavouritesUseCase,
    scheduler: RxScheduler
) : BasePresenter<FavouritesView>(scheduler), FavouritesPresenter {

    override fun start() {
        subscriptions().add(useCase.getFavourites()
            .compose(applySchedulers())
            .map { ServiceLocationUiMapper.map(it) }
            .doOnNext { ifViewAttached { view -> view.showFavourites(it) } }
            .doOnError { Timber.e(it) }
            .subscribe()
        )
    }

    override fun stop() {
        unsubscribe()
    }

}
